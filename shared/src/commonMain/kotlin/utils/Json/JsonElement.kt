import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

val JsonElement.asObject: JsonObject?
    get() = (this as? JsonObject)

val JsonElement.asArray: JsonArray?
    get() = (this as? JsonArray)

val JsonElement.asPrimitive: JsonPrimitive?
    get() = (this as? JsonPrimitive)