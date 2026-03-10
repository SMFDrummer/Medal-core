package io.github.smfdrummer.network.model

import kotlinx.serialization.Serializable

@Serializable
data class VerifySms(
    val phone: String,
    val smsType: String,
)

enum class SmsType(val smsType: String) {
    Register("register")
    ;
}