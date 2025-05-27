package io.github.smfdrummer.utils.strategy

import io.github.nomisrev.JsonPath
import io.github.nomisrev.path
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

interface ContextCallback {
    // 数据包执行状态回调
    fun onPacketStart(packetId: String)
    fun onPacketSuccess(packetId: String, response: JsonObject)
    fun onPacketFailure(packetId: String, error: StrategyException)
    fun onPacketRetry(packetId: String, attempt: Int, error: StrategyException)

    // 策略执行状态回调
    fun onStrategyComplete(success: Boolean)
}

class StrategyContext {
    val variables = mutableMapOf<String, JsonElement>()
    val responses = mutableMapOf<String, JsonObject>()
    internal var callback: ContextCallback? = null

    constructor()

    constructor(callback: ContextCallback?) {
        this.callback = callback
    }

    fun resolve(expression: String): JsonElement {
        val regex = """\{\{(?:([^.]+)\.)?([^}]+)\}\}""".toRegex()
        return regex.matchEntire(expression)?.let { match ->
            val (scope, path) = match.destructured
            when {
                scope.isEmpty() -> variables[path]
                else -> responses[scope]?.let { JsonPath.path(path).getOrNull(it) }
            } ?: JsonPrimitive("__MISSING_${expression}__")
        } ?: JsonPrimitive(expression)
    }
}