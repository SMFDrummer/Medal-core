package service.model

import kotlinx.serialization.Serializable

@Serializable
data class VerifySMS(
    val phone: String,
    val smsType: String,
)

enum class SmsType(val smsType: String) {
    Register("register")
    ;
}