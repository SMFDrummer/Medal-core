package io.github.smfdrummer.network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Requester
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal object BindPhone {
    suspend operator fun invoke(
        token: String,
        phone: String,
        userId: String,
        verifyCode: String
    ): Boolean = Requester.post(
        TalkwebHost.BIND_PHONE.url
    ) {
        setBody(
            FormDataContent(
                parameters {
                    append("token", token)
                    append("userId", userId)
                    append("phone", phone)
                    append("verifyCode", verifyCode)
                }
            )
        )
    }.let {
        when (it.status.value) {
            200 -> {
                val response = it.bodyAsText()
                when {
                    response.contains("0000") -> true
                    response.contains("验证码错误") -> false

                    else -> error(response)
                }
            }

            else -> error("BindPhone Failed (${it.status.value}: ${it.status.description})")
        }
    }
}

suspend fun bindPhone(
    token: String,
    phone: String,
    userId: String,
    verifyCode: String
): Boolean = BindPhone(token, phone, userId, verifyCode)