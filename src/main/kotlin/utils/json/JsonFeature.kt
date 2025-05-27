package io.github.smfdrummer.utils.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import java.util.*
import java.util.concurrent.ConcurrentHashMap

enum class JsonFeature {
    EncodeDefaults,
    PrettyPrint,
    ImplicitNulls,
    IgnoreUnknownKeys,
    AllowTrailingComma,
    AllowSpecialFloatingPointValues,
    UseDefaultNames,
    IsLenient
}

@OptIn(ExperimentalSerializationApi::class)
private fun JsonBuilder.configure(vararg features: JsonFeature): JsonBuilder {
    features.forEach {
        when (it) {
            JsonFeature.EncodeDefaults -> this.encodeDefaults = true
            JsonFeature.PrettyPrint -> this.prettyPrint = true
            JsonFeature.ImplicitNulls -> this.explicitNulls = false
            JsonFeature.IgnoreUnknownKeys -> this.ignoreUnknownKeys = true
            JsonFeature.AllowTrailingComma -> this.allowTrailingComma = true
            JsonFeature.AllowSpecialFloatingPointValues -> this.allowSpecialFloatingPointValues = true
            JsonFeature.UseDefaultNames -> this.useAlternativeNames = false
            JsonFeature.IsLenient -> this.isLenient = true
        }
    }
    return this
}

object JsonCache {
    private val cache = ConcurrentHashMap<EnumSet<JsonFeature>, Json>()

    fun getJson(vararg features: JsonFeature): Json {
        val featureSet = EnumSet.noneOf(JsonFeature::class.java).apply { addAll(features) }
        return cache.getOrPut(featureSet) {
            Json { configure(*features) }
        }
    }
}

fun jsonWith(vararg features: JsonFeature): Json = JsonCache.getJson(*features)

fun String.formatJsonString(vararg features: JsonFeature): String = jsonWith(*features).encodeToString(parseJson())
