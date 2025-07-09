package io.github.smfdrummer.network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Crypto.getMD5
import io.github.smfdrummer.network.CryptoDefaults.APPKEY
import io.github.smfdrummer.network.Requester
import io.github.smfdrummer.network.decryptTwPay
import io.github.smfdrummer.network.encryptTwPay
import io.github.smfdrummer.network.model.Login
import io.github.smfdrummer.network.model.headString
import io.github.smfdrummer.utils.json.getInt
import io.github.smfdrummer.utils.json.getString
import io.github.smfdrummer.utils.json.parseObject
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

object Login {
    suspend operator fun invoke(phoneOrUserId: String, passwordMD5: String): Pair<String, String> = Requester.post(TalkwebHost.LOGIN.url) {
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(
            FormDataContent(
                Parameters.build {
                    val loginString = Json.encodeToString(Login(password = passwordMD5, phone = phoneOrUserId))
                    listOf(
                        "head" to headString.encryptTwPay(),
                        "login" to loginString.encryptTwPay(),
                        "md5" to getMD5(loginString + APPKEY)
                    ).forEach { (key, value) -> append(key, value) }
                }
            )
        )
    }.let {
        when (it.status.value) {
            200 -> {
                val parse = it.bodyAsText().decryptTwPay().parseObject()
                if (parse.getString("resultCode") == "0000") {
                    with(parse.getString("content")!!.decryptTwPay().parseObject()) {
                        getInt("userId")!!.toString() to getString("token")!!
                    }
                } else error(parse.getString("content") ?: "未知错误")
            }

            else -> error("Login Failed (${it.status.value}: ${it.status.description})")
        }
    }
}

suspend fun login(phoneOrUserId: String, passwordMD5: String): Pair<String, String> = Login(phoneOrUserId, passwordMD5)