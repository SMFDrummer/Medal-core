package io.github.smfdrummer.utils.json

import kotlinx.serialization.json.*

/**
 * Parses any JSON [text] into [JsonElement]: object → [JsonObject], array → [JsonArray], value → [JsonPrimitive].
 */
fun Json.parse(text: String): JsonElement = parseToJsonElement(text)

/**
 * Parses this string into a [JsonElement] with optional [JsonFeature]s.
 */
fun String.parseJson(vararg features: JsonFeature): JsonElement = jsonWith(*features).parse(this)

/**
 * Parses any JSON [text] into a [JsonObject]. The text must be a valid JSON object starting with '{'.
 * @throws ClassCastException if the parsed element is not a JsonObject
 */
fun Json.parseObject(text: String): JsonObject {
    val element = parse(text)
    require(element is JsonObject) { "Expected JsonObject but got ${element::class.simpleName}" }
    return element
}

/**
 * Parses this string into a [JsonObject] with optional [JsonFeature]s.
 */
fun String.parseObject(vararg features: JsonFeature): JsonObject = jsonWith(*features).parseObject(this)

/**
 * Parses any JSON [text] into a [JsonArray]. The text must be a valid JSON array starting with '['.
 * @throws ClassCastException if the parsed element is not a JsonArray
 */
fun Json.parseArray(text: String): JsonArray {
    val element = parse(text)
    require(element is JsonArray) { "Expected JsonArray but got ${element::class.simpleName}" }
    return element
}

/**
 * Parses this string into a [JsonArray] with optional [JsonFeature]s.
 */
fun String.parseArray(vararg features: JsonFeature): JsonArray = jsonWith(*features).parseArray(this)

/**
 * Returns true if the [text] is a valid [JsonObject].
 */
fun Json.isObject(text: String): Boolean = runCatching { parse(text) is JsonObject }.getOrDefault(false)

/**
 * Returns true if this string is a valid [JsonObject].
 */
fun String.isJsonObject(): Boolean = Json.isObject(this)

/**
 * Returns true if the [text] is a valid [JsonArray].
 */
fun Json.isArray(text: String): Boolean = runCatching { parse(text) is JsonArray }.getOrDefault(false)

/**
 * Returns true if this string is a valid [JsonArray].
 */
fun String.isJsonArray(): Boolean = Json.isArray(this)

/**
 * Returns true if the [text] is a valid JSON (either [JsonObject] or [JsonArray]).
 */
fun Json.isValid(text: String): Boolean = runCatching {
    val element = parse(text)
    element is JsonObject || element is JsonArray
}.getOrDefault(false)

/**
 * Returns true if this string is a valid JSON (either [JsonObject] or [JsonArray]).
 */
fun String.isValidJson(): Boolean = Json.isValid(this)

/**
 * Deserializes JSON [text] to an object of type [T] using this [Json] instance.
 */
inline fun <reified T> Json.decode(text: String): T = decodeFromString(text)

/**
 * Deserializes this string to an object of type [T] with optional [JsonFeature]s.
 */
inline fun <reified T> String.fromJson(vararg features: JsonFeature): T = jsonWith(*features).decode(this)
inline fun <reified T> String.fromJsonOrNull(vararg features: JsonFeature): T? =
    runCatching { fromJson<T>(*features) }.getOrNull()
