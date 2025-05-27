package io.github.smfdrummer.utils.strategy

import arrow.core.Either
import arrow.core.raise.*
import io.github.nomisrev.JsonPath
import io.github.nomisrev.path
import io.github.smfdrummer.common.AndroidConfig
import io.github.smfdrummer.common.IOSConfig
import io.github.smfdrummer.common.platformConfig
import io.github.smfdrummer.network.Requester.request
import io.github.smfdrummer.network.UserProvider
import io.github.smfdrummer.network.decryptResponse
import io.github.smfdrummer.network.encryptRequest
import io.github.smfdrummer.utils.json.getIntValue
import io.github.smfdrummer.utils.json.parseObject
import io.github.smfdrummer.utils.json.primitive
import io.github.smfdrummer.utils.strategy.applications.androidCredential
import io.github.smfdrummer.utils.strategy.applications.iosCredential
import kotlinx.serialization.json.*

suspend fun StrategyConfig.execute(
    context: StrategyContext = StrategyContext()
) = with(StrategyManager(this@execute, context)) {
    executePackets()
}

class StrategyManager internal constructor(
    private val strategyConfig: StrategyConfig,
    private val context: StrategyContext
) {
    internal suspend fun executePackets(startIndex: Int = 0): Either<StrategyException, Boolean> = either {
        for (packet in strategyConfig.packets.drop(startIndex)) {
            if (packet.repeat < 1) continue

            val success = handleRepeat(packet).fold(
                {
                    when (it) {
                        is StrategyException.CredentialExpired,
                        is StrategyException.NetworkError,
                        is StrategyException.DecryptionError,
                        is StrategyException.UnexpectedResponseCode -> {
                            context.callback?.onPacketFailure(packet.i, it)
                            false
                        }

                        else -> raise(it)
                    }
                },
                {
                    context.callback?.onPacketSuccess(packet.i, context.responses[packet.i] ?: buildJsonObject { })
                    it
                }
            )

            if (!success) {
                val retryResult = handleRetry(packet).fold(
                    {
                        context.callback?.onPacketRetry(packet.i, packet.retry, it)
                        false
                    },
                    {
                        context.callback?.onPacketSuccess(packet.i, context.responses[packet.i] ?: buildJsonObject { })
                        it
                    }
                )
                if (!retryResult) {
                    packet.onFailure?.let {
                        if (!handleAction(it).bind()) {
                            context.callback?.onStrategyComplete(false)
                            return@either false
                        }
                    }
                }
            }

            packet.onSuccess?.let {
                if (!handleAction(it).bind()) {
                    context.callback?.onStrategyComplete(false)
                    return@either false
                }
            }
        }
        true
    }

    private suspend fun handleRepeat(packet: StrategyConfig.PacketConfig): Either<StrategyException, Boolean> = either {
        repeat(packet.repeat) {
            if (platformConfig is IOSConfig) {
                packet.t["ver_"] = primitive { platformConfig.channel.version }
            }
            val modified = packet.t.mapValues { (_, value) ->
                renderTemplate(value) { context.resolve(it) }
            }
            val result = (packet.i to JsonObject(modified))
                .apply { context.callback?.onPacketStart(this.first, this.second) }
                .sendPacket(packet).bind()
                .handleResponse(packet).bind()
            if (!result) {
                return@either false
            }
        }
        true
    }

    private suspend fun Pair<String, JsonObject>.sendPacket(packet: StrategyConfig.PacketConfig): Either<StrategyException, JsonObject> =
        either {
            encryptRequest().let { request ->
                catch({ request.request(platformConfig.host) }) { raise(StrategyException.NetworkError(it)) }
            }.let { response ->
                catch({ response.decryptResponse() }) { raise(StrategyException.DecryptionError(it)) }
            }.parseObject().also { response ->
                context.responses[packet.i] = response
                packet.extract.forEach { (variableName, jsonPath) ->
                    context.variables[variableName] = JsonPath.path(jsonPath).getOrNull(response) ?: JsonNull
                }
            }
        }

    private fun JsonObject.handleResponse(packetConfig: StrategyConfig.PacketConfig): Either<StrategyException, Boolean> =
        either {
            val r = getIntValue("r")
            ensure(r == packetConfig.r) {
                if (r == 20013 || r == 20020) StrategyException.CredentialExpired(r)
                else StrategyException.UnexpectedResponseCode(packetConfig.r, r)
            }
            true
        }

    private suspend fun handleRetry(packet: StrategyConfig.PacketConfig): Either<StrategyException, Boolean> = either {
        if (packet.retry < 1) return@either false

        repeat(packet.retry) { attempt ->
            handleRepeat(packet).fold(
                { context.callback?.onPacketRetry(packet.i, attempt, it) },
                { if (it) return@either true }
            )
        }

        raise(StrategyException.UnknownRetryError)
    }

    private suspend fun handleAction(onComplete: JsonPrimitive): Either<StrategyException, Boolean> = either {
        val content = onComplete.content
        when {
            content.toIntOrNull() != null -> {
                executePackets(content.toInt() - 1).fold(
                    { raise(it) },
                    { true }
                )
            }

            content.equals("true", ignoreCase = true) -> true
            content.equals("false", ignoreCase = true) -> false
            else -> raise(StrategyException.InvalidActionValue(content))
        }
    }

    private fun Raise<StrategyException.TemplateRenderError>.renderTemplate(
        template: JsonElement,
        resolve: (String) -> JsonElement
    ): JsonElement = when (template) {
        is JsonPrimitive -> catch({
            if (!template.isString) template
            val regex = """\{\{([^}]+)\}\}""".toRegex()
            if (!regex.containsMatchIn(template.content)) template
            if (with(template.content.trim()) { this.startsWith("{{") && this.endsWith("}}") }) resolve(template.content)
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

suspend fun StrategyConfig.executeWith(
    userProvider: UserProvider? = null,
    isRandom: Boolean = false,
    context: StrategyContext = StrategyContext()
): Either<StrategyException, Boolean> = either {
    if (!context.variables.containsKey("pi") &&
        !context.variables.containsKey("sk") &&
        !context.variables.containsKey("ui") &&
        userProvider != null
    ) {
        val (userId, token) = userProvider.fetch()

        when (platformConfig) {
            is AndroidConfig -> {
                androidCredential(userId, token).execute(context).bind()
            }

            is IOSConfig -> {
                iosCredential(userId, isRandom).execute(context).bind()
            }
        }
    }

    execute(context).fold(
        { error ->
            when (error) {
                is StrategyException.CredentialExpired -> {
                    ensureNotNull(userProvider) { error }
                    val (userId, token) = userProvider.fetch()
                    when (platformConfig) {
                        is AndroidConfig -> {
                            androidCredential(userId, token).execute(context).bind()
                        }

                        is IOSConfig -> {
                            iosCredential(userId, isRandom).execute(context).bind()
                        }
                    }

                    execute(context).bind()
                }

                else -> raise(error)
            }
        },
        { success -> success }
    )
}