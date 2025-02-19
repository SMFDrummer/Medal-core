package service

import api.APPKEY
import api.TalkwebHost
import getString
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import packet.Crypto
import packet.client
import service.model.SmsType
import service.model.VerifySMS
import service.model.headString

object GenVerifySms {
    suspend operator fun invoke(phone: String, smsType: SmsType = SmsType.Register): Boolean =
        client.post(TalkwebHost.GEN_VERIFY_SMS.url) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        val verifySMS = Json.encodeToString(VerifySMS(phone = phone, smsType = smsType.smsType))
                        listOf(
                            "head" to Crypto.TwPay.encrypt(headString),
                            "verifySMS" to Crypto.TwPay.encrypt(verifySMS),
                            "md5" to Crypto.getMD5(verifySMS + APPKEY)
                        ).forEach { (key, value) -> append(key, value) }
                    }
                )
            )
        }.let {
            return when (it.status.value) {
                200 -> (Json.parseToJsonElement(Crypto.TwPay.decrypt(it.bodyAsText())) as? JsonObject)
                    ?.let { response ->
                        response.getString("resultCode")!! == "0000"
                    } == true

                else -> false
            }
        }
}

suspend fun genVerifySms(phone: String, smsType: SmsType = SmsType.Register): Boolean = GenVerifySms(phone, smsType)