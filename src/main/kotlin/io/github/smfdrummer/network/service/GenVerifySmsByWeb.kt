package io.github.smfdrummer.network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Requester
import io.github.smfdrummer.network.model.VerifyType
import io.github.smfdrummer.utils.json.asObject
import io.github.smfdrummer.utils.json.getString
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

internal object GenVerifySmsByWeb {
    suspend operator fun invoke(
        token: String,
        phone: String,
        userId: String,
        verifyType: VerifyType = VerifyType.BindPhone
    ): Boolean = Requester.post(TalkwebHost.GEN_VERIFY_SMS_BY_WEB.url) {
        setBody(
            FormDataContent(
                parameters {
                    append("token", token)
                    append("phone", phone)
                    append("userId", userId)
                    append("verifyType", verifyType.verifyType)
                }
            )
        )
    }.let {
        return when (it.status.value) {
            200 -> Json.parseToJsonElement(it.bodyAsText()).asObject?.getString("resultCode") == "0000"

            else -> false
        }
    }
}

suspend fun genVerifySmsByWeb(
    token: String,
    phone: String,
    userId: String,
    verifyType: VerifyType = VerifyType.BindPhone
) = GenVerifySmsByWeb(token, phone, userId, verifyType)
