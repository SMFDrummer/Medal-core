package io.github.smfdrummer.utils.json

/**
 * JsonArray 扩展函数：提供带空安全的和强制类型转换的取值操作
 */

import kotlinx.serialization.json.*

// region Safe Getters

fun JsonArray.getInt(index: Int): Int? =
    getOrNull(index)?.jsonPrimitive?.intOrNull

fun JsonArray.getLong(index: Int): Long? =
    getOrNull(index)?.jsonPrimitive?.longOrNull

fun JsonArray.getBoolean(index: Int): Boolean? =
    getOrNull(index)?.jsonPrimitive?.booleanOrNull

fun JsonArray.getFloat(index: Int): Float? =
    getOrNull(index)?.jsonPrimitive?.floatOrNull

fun JsonArray.getDouble(index: Int): Double? =
    getOrNull(index)?.jsonPrimitive?.doubleOrNull

fun JsonArray.getString(index: Int): String? =
    getOrNull(index)?.jsonPrimitive?.contentOrNull

// endregion

// region Strict Getters (Throw if missing or wrong type)

fun JsonArray.getIntValue(index: Int): Int =
    getOrThrow(index).jsonPrimitive.int

fun JsonArray.getLongValue(index: Int): Long =
    getOrThrow(index).jsonPrimitive.long

fun JsonArray.getBooleanValue(index: Int): Boolean =
    getOrThrow(index).jsonPrimitive.boolean

fun JsonArray.getFloatValue(index: Int): Float =
    getOrThrow(index).jsonPrimitive.float

fun JsonArray.getDoubleValue(index: Int): Double =
    getOrThrow(index).jsonPrimitive.double

// endregion

/**
 * 获取指定索引的 JsonObject
 * @throws IndexOutOfBoundsException or ClassCastException
 */
fun JsonArray.getJsonObject(index: Int): JsonObject =
    getOrThrow(index).jsonObject

/**
 * 获取指定索引的 JsonPrimitive
 * @throws IndexOutOfBoundsException or ClassCastException
 */
fun JsonArray.getJsonPrimitive(index: Int): JsonPrimitive =
    getOrThrow(index).jsonPrimitive

/**
 * 转为 JSON 字符串，可指定序列化特性
 */
fun JsonArray.toJsonString(vararg features: JsonFeature): String = jsonWith(*features).encodeToString(this)

/**
 * 修改指定索引上的值，支持批量修改
 * @param modifications Pair(index, newValue)
 * @return 新 JsonArray
 */
fun JsonArray.modify(vararg modifications: Pair<Int, JsonElement>): JsonArray =
    JsonArray(this.mapIndexed { index, element ->
        modifications.firstOrNull { it.first == index }?.second ?: element
    })

/**
 * 在数组开头添加元素
 */
fun JsonArray.addFirst(vararg elements: JsonElement): JsonArray =
    JsonArray(elements.asList() + this)

/**
 * 在数组末尾添加元素
 */
fun JsonArray.addLast(vararg elements: JsonElement): JsonArray =
    JsonArray(this + elements.asList())

/**
 * 安全获取 index 元素，越界时抛出异常并指明位置
 */
private fun JsonArray.getOrThrow(index: Int): JsonElement =
    getOrNull(index)
        ?: throw IndexOutOfBoundsException("Index $index is out of bounds for JsonArray of size $size")
