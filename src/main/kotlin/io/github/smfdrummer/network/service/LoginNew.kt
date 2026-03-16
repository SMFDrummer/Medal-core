package io.github.smfdrummer.network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Crypto.APPKEY
import io.github.smfdrummer.network.Crypto.getMD5
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

internal object LoginNew {
    suspend operator fun invoke(phoneOrUserId: String, password: String): Pair<String, String> =
        Requester.post(TalkwebHost.LOGIN_NEW.url) {
            setBody(
                FormDataContent(
                    parameters {
                        val loginString = Json.encodeToString(Login(password = password, phone = phoneOrUserId))
                        append("head", headString.encryptTwPay())
                        append("login", loginString.encryptTwPay())
                        append("md5", getMD5(loginString + APPKEY))
                    }
                )
            )
        }.let {
            when (it.status.value) {
                200 -> {
                    val parse = it.bodyAsText().decryptTwPay().parseObject()
                    if (parse.getString("resultCode") == "6666") {
                        with(parse.getString("content")!!.decryptTwPay().parseObject()) {
                            getInt("userId")!!.toString() to getString("token")!!
                        }
                    } else error(parse.getString("content") ?: "未知错误")
                }

                else -> error("Login Failed (${it.status.value}: ${it.status.description})")
            }
        }
}

suspend fun loginNew(phoneOrUserId: String, password: String): Pair<String, String> = LoginNew(phoneOrUserId, password)
