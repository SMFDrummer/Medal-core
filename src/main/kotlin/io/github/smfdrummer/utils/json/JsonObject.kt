package io.github.smfdrummer.utils.json

/**
 * 扩展 [JsonObject] 的便捷工具函数。
 * - getX: 安全获取值，找不到或类型错误时返回 null
 * - getXValue: 获取值，失败会抛出异常
 * - removeKeys: 删除指定键
 * - decodeToMap: 统一解码 JsonObject 为 Map<String, T>
 */

import kotlinx.serialization.json.*

//region get (Safe nullable access)

fun JsonObject.getInt(key: String): Int? = this[key]?.jsonPrimitive?.intOrNull
fun JsonObject.getLong(key: String): Long? = this[key]?.jsonPrimitive?.longOrNull
fun JsonObject.getBoolean(key: String): Boolean? = this[key]?.jsonPrimitive?.booleanOrNull
fun JsonObject.getFloat(key: String): Float? = this[key]?.jsonPrimitive?.floatOrNull
fun JsonObject.getDouble(key: String): Double? = this[key]?.jsonPrimitive?.doubleOrNull
fun JsonObject.getString(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

//endregion

//region getValue (Throws on failure)

fun JsonObject.getIntValue(key: String): Int =
    this[key]?.jsonPrimitive?.int ?: error("Key '$key' is missing or not an Int")

fun JsonObject.getLongValue(key: String): Long =
    this[key]?.jsonPrimitive?.long ?: error("Key '$key' is missing or not a Long")

fun JsonObject.getBooleanValue(key: String): Boolean =
    this[key]?.jsonPrimitive?.boolean ?: error("Key '$key' is missing or not a Boolean")

fun JsonObject.getFloatValue(key: String): Float =
    this[key]?.jsonPrimitive?.float ?: error("Key '$key' is missing or not a Float")

fun JsonObject.getDoubleValue(key: String): Double =
    this[key]?.jsonPrimitive?.double ?: error("Key '$key' is missing or not a Double")

//endregion

//region Structural access

fun JsonObject.getJsonObject(key: String): JsonObject? =
    this[key]?.jsonObject

fun JsonObject.getJsonArray(key: String): JsonArray? =
    this[key]?.jsonArray

fun JsonObject.getJsonPrimitive(key: String): JsonPrimitive? =
    this[key]?.jsonPrimitive

//endregion

//region Utilities

fun JsonObject.containsKey(vararg keys: String): Boolean =
    keys.all { this.containsKey(it) }

fun JsonObject.removeKeys(vararg keys: String): JsonObject =
    JsonObject(this.filterKeys { it !in keys })

fun JsonObject.toJsonString(vararg features: JsonFeature): String =
    jsonWith(*features).encodeToString(this)

inline fun <reified T> JsonObject.decodeToMap(): Map<String, T> {
    val json = jsonWith(
        JsonFeature.IgnoreUnknownKeys,
        JsonFeature.EncodeDefaults,
        JsonFeature.UseDefaultNames,
        JsonFeature.AllowTrailingComma
    )
    return mapValues { (_, value) -> json.decodeFromJsonElement<T>(value) }
}

//endregion
