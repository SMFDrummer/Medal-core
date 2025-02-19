package packet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Credential(
    @SerialName("pi") val personalId: String,
    @SerialName("sk") val securityKey: String,
    @SerialName("ui") val userId: String,
)
