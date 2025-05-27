package io.github.smfdrummer.network.model

import io.github.smfdrummer.common.platformConfig
import io.github.smfdrummer.utils.json.JsonFeature
import io.github.smfdrummer.utils.json.jsonWith
import kotlinx.serialization.Serializable
import kotlin.getValue

@Serializable
data class Head(
    val appId: Int = platformConfig.channel.appId,
    val channelId: Int = platformConfig.channel.channelId,
    val sdkVersion: String = "2.0.0",
)

val headString by lazy {
    jsonWith(JsonFeature.EncodeDefaults).encodeToString(Head())
}