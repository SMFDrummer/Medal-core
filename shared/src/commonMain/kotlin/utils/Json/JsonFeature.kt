import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import java.util.concurrent.ConcurrentHashMap

enum class JsonFeature {
    EncodeDefaults,
    PrettyPrint,
    ImplicitNulls,
    IgnoreUnknownKeys,
    AllowTrailingComma,
    AllowSpecialFloatingPointValues,
    UseDefaultNames
}

@OptIn(ExperimentalSerializationApi::class)
private fun JsonBuilder.config(vararg features: JsonFeature): JsonBuilder {
    features.forEach {
        when (it) {
            JsonFeature.EncodeDefaults -> this.encodeDefaults = true
            JsonFeature.PrettyPrint -> this.prettyPrint = true
            JsonFeature.ImplicitNulls -> this.explicitNulls = false
            JsonFeature.IgnoreUnknownKeys -> this.ignoreUnknownKeys = true
            JsonFeature.AllowTrailingComma -> this.allowTrailingComma = true
            JsonFeature.AllowSpecialFloatingPointValues -> this.allowSpecialFloatingPointValues = true
            JsonFeature.UseDefaultNames -> this.useAlternativeNames = false
        }
    }
    return this
}

class JsonCache {
    private val cache = ConcurrentHashMap<Set<JsonFeature>, Json>()

    fun getJson(vararg features: JsonFeature): Json {
        val featureSet = features.toSet()
        return cache.getOrPut(featureSet) {
            Json { config(*features) }
        }
    }
}

private val jsonCache = JsonCache()

fun Json.by(vararg features: JsonFeature): Json = jsonCache.getJson(*features)

fun String.toJsonString(vararg features: JsonFeature): String = Json.by(*features).encodeToString(this.parseJson())
