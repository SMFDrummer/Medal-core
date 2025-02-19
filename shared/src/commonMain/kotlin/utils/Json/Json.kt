import kotlinx.serialization.json.*

/**
 *  utils.Json.parse any text to [JsonElement], such value to [JsonPrimitive], object to [JsonObject], array to [JsonArray]
 */
fun Json.parse(text: String): JsonElement = this.parseToJsonElement(text)

fun String.parseJson(vararg features: JsonFeature): JsonElement = Json.by(*features).parse(this)

/**
 *  utils.Json.parse any text to [JsonObject], the text must be a [LinkedHashMap] start with '{' and end with '}'
 */
fun Json.parseObject(text: String): JsonObject = this.parse(text) as JsonObject

fun String.parseObject(vararg features: JsonFeature): JsonObject = Json.by(*features).parseObject(this)

/**
 *  utils.Json.parse any text to [JsonArray], the text must be a [LinkedHashSet] start with '[' and end with ']'
 */
fun Json.parseArray(text: String): JsonArray = this.parse(text) as JsonArray

fun String.parseArray(vararg features: JsonFeature): JsonArray = Json.by(*features).parseArray(this)

/**
 * if text is a [JsonObject], return true, else return false
 */
fun Json.isObject(text: String): Boolean {
    return try {
        this.parse(text) is JsonObject
    } catch (_: Exception) {
        false
    }
}

fun String.isJsonObject(): Boolean = Json.isObject(this)

/**
 * if text is a [JsonArray], return true, else return false
 */
fun Json.isArray(text: String): Boolean {
    return try {
        this.parse(text) is JsonArray
    } catch (_: Exception) {
        false
    }
}

fun String.isJsonArray(): Boolean = Json.isArray(this)

/**
 * if text is a [JsonObject] or a [JsonArray], return true, else return false
 */
fun Json.isValid(text: String): Boolean {
    return try {
        val parse = this.parse(text)
        parse is JsonObject || parse is JsonArray
    } catch (_: Exception) {
        false
    }
}

fun String.isValidJson(): Boolean = Json.isValid(this)

/**
 * deserialize any text to target class
 */
inline fun <reified T> Json.to(text: String): T = this.decodeFromString<T>(text)

inline fun <reified T> String.to(vararg features: JsonFeature): T = Json.by(*features).to<T>(this)
