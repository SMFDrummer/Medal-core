package service

import api.APPKEY
import api.TalkwebHost
import api.logger
import getString
import io.github.nomisrev.JsonPath
import io.github.nomisrev.select
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import packet.Crypto
import packet.client
import parseObject
import primitive
import service.model.RegisterInfo
import service.model.User
import service.model.headString
import java.util.*
import kotlin.random.Random

@Deprecated("Talkweb Register deprecated: Because of ip lock need send 1w+ request to break sms verify code")
object Register {
    fun getRandomPhone(): String = StringBuilder().apply {
        append("1")
        append(Random.nextInt(3, 10))
        repeat(9) { append(Random.nextInt(10)) }
    }.toString()

    fun getRandomPassword(): String = UUID.randomUUID().toString().split("-").last()

    suspend operator fun invoke(phone: String = getRandomPhone(), password: String = getRandomPassword()): User {
        suspend fun getResponse(i: Int): User? = client.post(TalkwebHost.REGISTER.url) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        val registerInfo = Json.encodeToString(
                            RegisterInfo(
                                password = Crypto.getMD5(password),
                                phone = phone,
                                smsVerifyCode = i.toString().padStart(4, '0')
                            )
                        )
                        listOf(
                            "head" to Crypto.TwPay.encrypt(headString),
                            "registerInfo" to Crypto.TwPay.encrypt(registerInfo),
                            "md5" to Crypto.getMD5(registerInfo + APPKEY)
                        ).forEach { (key, value) -> append(key, value) }
                    }
                )
            )
        }.let {
            return@let when (it.status.value) {
                200 -> (Json.parseToJsonElement(
                    Crypto.TwPay.decrypt(it.bodyAsText()).apply { println(this) }) as? JsonObject)
                    ?.let { response ->
                        when (response.getString("resultCode")!!) {
                            "0090", "0095" -> null // 签名错误、验证码错误
                            "0091" -> throw UserAlreadyExistsException() // 用户已存在
                            "0000" -> {
                                logger.info("注册成功")
                                Crypto.TwPay.decrypt(response.getString("content")!!).parseObject().let {
                                    JsonPath.select("phone").modify(it) { primitive { phone } }
                                }.let {
                                    JsonPath.select("password").modify(it) { primitive { password } }
                                }.let {
                                    Json.decodeFromJsonElement(it)
                                }
                            }

                            else -> null
                        }
                    }

                else -> null
            }
        }

        suspend fun fetchResult(): User? = coroutineScope {
            val result = CompletableDeferred<User?>()
            val dispatcher = Dispatchers.IO.limitedParallelism(34)

            val jobs = (0..9999).map { number ->
                async(dispatcher) {
                    if (result.isCompleted) return@async
                    val user = getResponse(number)
                    if (user != null) {
                        result.complete(user)
                    }
                }
            }

            try {
                result.await()
            } finally {
                jobs.forEach { it.cancelAndJoin() }
            }
        }

        return check(genVerifySms(phone)) { "获取验证码失败" }
            .let { fetchResult() }
            ?: error("注册失败")
    }
}

private suspend fun register(
    phone: String = Register.getRandomPhone(),
    password: String = Register.getRandomPassword()
): User = Register(phone, password)

private suspend fun registerStrict(): User {
    var user: User
    while (true) {
        try {
            user = Register()
            break
        } catch (e: Exception) {
            logger.warn(e.message)
        }
    }
    return user
}

private class UserAlreadyExistsException : Exception("用户已存在")