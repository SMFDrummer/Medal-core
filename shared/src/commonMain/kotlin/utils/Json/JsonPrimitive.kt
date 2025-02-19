import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

class JsonPrimitiveBuilder @PublishedApi internal constructor() {
    var value: JsonPrimitive = JsonNull

    operator fun <T> T.unaryPlus() where T : Any? {
        value = when (this) {
            null -> JsonNull
            is Int -> JsonPrimitive(this)
            is Long -> JsonPrimitive(this)
            is Double -> JsonPrimitive(this)
            is Float -> JsonPrimitive(this.toDouble())
            is String -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is java.math.BigInteger -> JsonPrimitive(this.toString())
            is java.math.BigDecimal -> JsonPrimitive(this.toString())
            else -> throw IllegalArgumentException("Unsupported value: $this")
        }
    }
}

inline fun primitive(block: JsonPrimitiveBuilder.() -> Any?): JsonPrimitive {
    val builder = JsonPrimitiveBuilder()
    builder.apply { +block() }
    return builder.value
}

