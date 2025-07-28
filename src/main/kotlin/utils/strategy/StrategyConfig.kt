package io.github.smfdrummer.utils.strategy

import io.github.smfdrummer.utils.json.JsonFeature
import io.github.smfdrummer.utils.json.jsonWith
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class StrategyConfig(
    val version: Int = 1,
    val description: String = "null",
    val packets: List<PacketConfig>
) {
    @Serializable
    data class PacketConfig(
        val i: String,
        val r: Int = 0,
        val t: MutableMap<String, JsonElement> = linkedMapOf(),
        val ev: Int = 2,
        val repeat: Int = 1,
        val retry: Int = 0,
        val extract: Map<String, String> = emptyMap(),
        val onSuccess: JsonPrimitive? = null,
        val onFailure: JsonPrimitive? = null,
    )

    override fun toString(): String = "版本：${version}\n描述：${description}"

    fun toJsonString(): String = jsonWith(
        JsonFeature.ImplicitNulls,
        JsonFeature.EncodeDefaults,
        JsonFeature.PrettyPrint
    ).encodeToString(this)
}
