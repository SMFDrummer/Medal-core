package service.model

import kotlinx.serialization.Serializable

/**
 * @see service.Register
 */
@Deprecated("Refer Register")
@Serializable
data class RegisterInfo(
    val password: String,
    val phone: String,
    val registerImei: String = "",
    val smsVerifyCode: String,
)