package io.github.smfdrummer.network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Crypto
import io.github.smfdrummer.network.Crypto.APPKEY
import io.github.smfdrummer.network.Requester
import io.github.smfdrummer.network.model.SmsType
import io.github.smfdrummer.network.model.VerifySms
import io.github.smfdrummer.network.model.headString
import io.github.smfdrummer.utils.json.asObject
import io.github.smfdrummer.utils.json.getString
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

internal object GenVerifySms {
    suspend operator fun invoke(phone: String, smsType: SmsType = SmsType.Register): Boolean =
        Requester.post(TalkwebHost.GEN_VERIFY_SMS.url) {
            setBody(
                FormDataContent(
                    parameters {
                        val verifySMS = Json.encodeToString(VerifySms(phone = phone, smsType = smsType.smsType))
                        append("head", Crypto.TwPay.encrypt(headString))
                        append("verifySMS", Crypto.TwPay.encrypt(verifySMS))
                        append("md5", Crypto.getMD5(verifySMS + APPKEY))
                    }
                )
            )
        }.let {
            return when (it.status.value) {
                200 -> Json.parseToJsonElement(Crypto.TwPay.decrypt(it.bodyAsText())).asObject
                    ?.let { response -> response.getString("resultCode")!! == "0000" } == true

                else -> false
            }
        }
}

suspend fun genVerifySms(phone: String, smsType: SmsType = SmsType.Register): Boolean = GenVerifySms(phone, smsType)
