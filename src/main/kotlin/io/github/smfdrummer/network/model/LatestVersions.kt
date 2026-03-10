package io.github.smfdrummer.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestVersions(
    val queryDateTime: String,
    val latestVersions: List<LatestVersion>
) {
    @Serializable
    data class LatestVersion(
        @SerialName("qudao") val channel: String,
        val version: String,
        val status: Int
    )
}