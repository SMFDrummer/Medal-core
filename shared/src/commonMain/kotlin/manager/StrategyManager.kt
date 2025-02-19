package manager

import api.logger
import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensure
import asObject
import config.IOSConfig
import config.StrategyConfig
import exception.StrategyException
import exception.StrategyExceptionWrapper
import getIntValue
import io.github.nomisrev.JsonPath
import io.github.nomisrev.path
import io.github.nomisrev.select
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.*
import packet.Context
import packet.Crypto
import packet.request
import parseObject
import primitive
import service.model.User
import service.model.packets

suspend fun StrategyConfig.execute(user: User, contextHandler: (Context.() -> Unit)? = null): Boolean {
    with(StrategyManager().init(this)) {
        logger.info("执行策略: $strategy")

        return user.scope.async {
            user.lock.withLock {
                user.handleAction(user.execute(0).fold({ primitive { false } }, { primitive { true } }))
            }
        }.await().apply {
            contextHandler?.let {
                context.contextHandler()
            }
        }
    }
}

class StrategyManager internal constructor() {
    lateinit var strategy: StrategyConfig
    lateinit var context: Context

    internal fun init(strategy: StrategyConfig): StrategyManager {
        this.strategy = strategy
        this.context = Context()
        return this
    }

    private suspend fun User.handleRepeat(packetConfig: StrategyConfig.PacketConfig): Either<StrategyException, Unit> =
        either {
            repeat(packetConfig.repeat) {
                packets[packetConfig.i]
                    ?.build()
                    ?.buildModifyPacket(packetConfig)?.bind()
                    ?.sendPacket(packetConfig)?.bind()
                    ?.handleResponse(packetConfig)?.bind()
                    ?: raise(StrategyException.PacketNotFound)
            }
        }

    private fun Pair<String, JsonObject>.buildModifyPacket(packetConfig: StrategyConfig.PacketConfig): Either<StrategyException, Pair<String, JsonObject>> =
        either {
            if (config is IOSConfig) {
                packetConfig.t.put("ver_", primitive { config.channel.version })
            }
            packetConfig.t.forEach { (key, value) ->
                packetConfig.t[key] = renderTemplate(value) { context.resolve(it) }
            }
            (first to packetConfig.t.entries.fold(second as JsonElement) { acc, (key, value) ->
                catch({ JsonPath.select(key).modify(acc) { value } }) {
                    raise(StrategyException.JsonModificationError(key, it))
                }
            }.asObject!!).apply { logger.trace("[Send] || [{}] {}", first, second) }
        }

    private suspend fun Pair<String, JsonObject>.sendPacket(packetConfig: StrategyConfig.PacketConfig): Either<StrategyException, JsonObject> =
        either {
            Crypto.Request.encrypt(this@sendPacket).let { request ->
                catch({ request.request(config.host) }) { raise(StrategyException.NetworkError(it)) }
            }.let { response ->
                catch({ Crypto.Response.decrypt(response) }) { raise(StrategyException.DecryptionError(it)) }
            }.apply { logger.trace("[Recv] || {}", this) }.parseObject().also { response ->
                context.responses[packetConfig.i] = response
                packetConfig.extract.forEach { (variableName, jsonPath) ->
                    context.variables[variableName] = JsonPath.path(jsonPath).getOrNull(response) ?: JsonNull
                }
            }
        }

    private fun JsonObject.handleResponse(packetConfig: StrategyConfig.PacketConfig): Either<StrategyException, Unit> =
        either {
            val r = getIntValue("r")
            ensure(r == packetConfig.r) {
                if (r == 20013 || r == 20020) StrategyException.CredentialExpired(r)
                else StrategyException.UnexpectedResponseCode(packetConfig.r, r)
            }
        }

    private suspend fun User.handleRetry(packetConfig: StrategyConfig.PacketConfig): Boolean {
        var success = false
        repeat(packetConfig.retry) { attempt ->
            success = handleRepeat(packetConfig).fold({ false }, { true })
            if (success) return@repeat
        }
        return success
    }

    internal suspend fun User.execute(startIndex: Int = 0): Either<StrategyException, JsonPrimitive> = either {
        for (packet in strategy.packets.drop(startIndex)) {
            if (packet.repeat < 1) continue
            var success = handleRepeat(packet).fold(
                {
                    when (it) {
                        is StrategyException.CredentialExpired -> {
                            logger.info("账号: ${this@execute.userId} 登陆凭据失效，尝试重新获取")
                            catch({ refreshCredential().join() }) { raise(StrategyException.CredentialRefreshError(it)) }
                            handleRepeat(packet).fold({ false }, { true })
                        }

                        is StrategyException.NetworkError,
                        is StrategyException.DecryptionError,
                        is StrategyException.UnexpectedResponseCode -> false

                        else -> raise(it)
                    }
                },
                { true }
            )
            if (!success) success = handleRetry(packet)
            if (success) {
                packet.onSuccess ?: continue
            } else {
                packet.onFailure ?: continue
            }
        }
        primitive { true }
    }

    internal suspend fun User.handleAction(jsonPrimitive: JsonPrimitive): Boolean = try {
        val content = jsonPrimitive.content
        val intValue = content.toIntOrNull()
        when {
            intValue != null -> {
                handleAction(execute(intValue - 1).fold({ throw StrategyExceptionWrapper(it) }, { it }))
            }

            content.equals("true", ignoreCase = true) || content.equals("false", ignoreCase = true) -> {
                content.toBoolean()
            }

            else -> error("Unexpected value $content")
        }
    } catch (e: Exception) {
        logger.error("Error handleAction: ${e.message}")
        false
    }

    private fun Raise<StrategyException.TemplateRenderError>.renderTemplate(
        template: JsonElement,
        resolve: (String) -> JsonElement
    ): JsonElement = when (template) {
        is JsonPrimitive -> catch({
            if (!template.isString) template
            val regex = """\$\{([^}]+)}""".toRegex()
            if (!regex.containsMatchIn(template.content)) template
            if (with(template.content.trim()) { this.startsWith("\${") && this.endsWith("}") }) resolve(template.content)
            else primitive {
                template.content.replace(regex) { matchResult ->
                    resolve(matchResult.groupValues[1]).let {
                        if (it is JsonPrimitive && it.isString) it.content else it.toString()
                    }
                }
            }
        }) {
            raise(StrategyException.TemplateRenderError(template.content, it))
        }

        is JsonObject -> buildJsonObject {
            template.forEach { (key, value) ->
                put(key, renderTemplate(value, resolve))
            }
        }

        is JsonArray -> buildJsonArray {
            template.forEach { add(renderTemplate(it, resolve)) }
        }
    }
}