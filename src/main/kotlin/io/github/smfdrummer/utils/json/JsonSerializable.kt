package io.github.smfdrummer.utils.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

interface JsonSerializable

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
inline fun <reified T : JsonSerializable> T.toJsonString(vararg features: JsonFeature): String {
    return jsonWith(*features).encodeToString(T::class.serializer(), this)
}