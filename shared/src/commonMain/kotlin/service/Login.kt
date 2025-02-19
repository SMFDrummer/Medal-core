package service

import api.APPKEY
import api.TalkwebHost
import getString
import io.github.nomisrev.JsonPath
import io.github.nomisrev.select
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import packet.Crypto
import packet.client
import parseObject
import primitive
import service.model.Login
import service.model.User
import service.model.headString

object Login {
    suspend operator fun invoke(phoneOrUserId: String, passwordMD5: String): User = client.post(TalkwebHost.LOGIN.url) {
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(
            FormDataContent(
                Parameters.build {
                    val loginString = Json.encodeToString(Login(password = passwordMD5, phone = phoneOrUserId))
                    listOf(
                        "head" to Crypto.TwPay.encrypt(headString),
                        "login" to Crypto.TwPay.encrypt(loginString),
                        "md5" to Crypto.getMD5(loginString + APPKEY)
                    ).forEach { (key, value) -> append(key, value) }
                }
            )
        )
    }.let {
        when (it.status.value) {
            200 -> {
                val parse = Json.parseObject(Crypto.TwPay.decrypt(it.bodyAsText()))
                if (parse.getString("resultCode") == "0000") {
                    Json.parseObject(Crypto.TwPay.decrypt(parse.getString("content")!!)).let {
                        JsonPath.select("phone").modify(it) { primitive { phoneOrUserId } }
                    }.let {
                        JsonPath.select("password").modify(it) { primitive { passwordMD5 } }
                    }.let {
                        Json.decodeFromJsonElement(it)
                    }
                } else error(parse.getString("content") ?: "未知错误")
            }

            else -> error("Login Failed (${it.status.value}: ${it.status.description})")
        }
    }
}

suspend fun login(phoneOrUserId: String, passwordMD5: String): User = Login(phoneOrUserId, passwordMD5)