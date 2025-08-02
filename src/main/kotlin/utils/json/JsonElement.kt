@file:OptIn(ExperimentalSerializationApi::class)

package io.github.smfdrummer.utils.json

import arrow.core.escaped
import arrow.optics.Copy
import io.github.nomisrev.JsonPath
import io.github.nomisrev.path
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*

/**
 * 安全转换为 [JsonObject]，如果当前元素不是一个对象类型，则返回 `null`。
 */
val JsonElement.asObject: JsonObject?
    get() = this as? JsonObject

/**
 * 安全转换为 [JsonArray]，如果当前元素不是一个数组类型，则返回 `null`。
 */
val JsonElement.asArray: JsonArray?
    get() = this as? JsonArray

/**
 * 安全转换为 [JsonPrimitive]，如果当前元素不是一个基础值类型，则返回 `null`。
 */
val JsonElement.asPrimitive: JsonPrimitive?
    get() = this as? JsonPrimitive

/**
 * 将当前 [JsonElement] 转换为带指定精度的小数 [JsonUnquotedLiteral]。
 *
 * @param scale 保留的小数位数，默认为 6。
 * @return 转换后的 [JsonPrimitive]，为未经转义的 JSON 字符串。
 *
 * 示例：`"3.1415926535"` -> `3.141593`
 */
fun JsonElement.toScaledLiteral(scale: Int = 6): JsonPrimitive =
    JsonUnquotedLiteral(this.jsonPrimitive.content.toBigDecimal().setScale(scale).toString())

/**
 * 将当前字符串添加双引号（"）包裹。
 */
private fun String.addQuotes(): String = "\"$this\""

/**
 * 将当前字符串中的每个字符编码为 `\\uXXXX` 格式，确保为 ASCII 字符串。
 */
private fun String.ensureAscii(): String = this.toCharArray().joinToString("") {
    "\\u" + it.code.toString(16).padStart(4, '0')
}

/**
 * 将当前 [JsonElement] 转换为纯 ASCII 表达的字符串 [JsonUnquotedLiteral]，并添加引号。
 *
 * 通常用于需要安全显示或网络传输的 JSON 字段值。
 */
fun JsonElement.toAsciiLiteral(): JsonPrimitive =
    JsonUnquotedLiteral(this.jsonPrimitive.content.ensureAscii().escaped().addQuotes())


fun Copy<JsonElement>.path(path: String, transformer: (JsonElement) -> JsonElement) {
    JsonPath.path(path).transform(transformer)
}

fun JsonElement.toJsonString(vararg features: JsonFeature): String = when (this) {
    is JsonObject -> toJsonString(*features)
    is JsonArray -> toJsonString(*features)
    else -> toString()
}

inline fun <reified T> JsonElement.fromJson(vararg features: JsonFeature): T =
    jsonWith(*features).decodeFromJsonElement<T>(this)

inline fun <reified T> JsonElement.fromJsonOrNull(vararg features: JsonFeature): T? =
    runCatching { fromJson<T>(*features) }.getOrNull()