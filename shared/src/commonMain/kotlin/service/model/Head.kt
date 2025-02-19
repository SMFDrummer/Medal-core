package service.model

import JsonFeature
import by
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import manager.config

@Serializable
data class Head(
    val appId: Int = config.channel.appId,
    val channelId: Int = config.channel.channelId,
    val sdkVersion: String = "2.0.0",
)

val headString by lazy {
    Json.by(JsonFeature.EncodeDefaults).encodeToString(Head())
}
