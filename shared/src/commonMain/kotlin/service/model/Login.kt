package service.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val identifyCode: String? = null,
    val password: String,
    val phone: String,
    val token: String? = null,
    val type: String? = null,
    val userId: Long? = null,
)
