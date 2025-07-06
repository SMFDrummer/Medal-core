package network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Requester
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

object ModifyPassword {
    suspend operator fun invoke(token: String, userId: String, oldPassword: String, newPassword: String): Boolean =
        Requester.post(
            TalkwebHost.MODIFY_PASSWORD.url
        ) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        listOf(
                            "token" to token,
                            "userId" to userId,
                            "oldPassword" to oldPassword,
                            "newPassword" to newPassword
                        ).forEach { (key, value) -> append(key, value) }
                    }
                )
            )
        }.let {
            when (it.status.value) {
                200 -> {
                    val response = it.bodyAsText()
                    when {
                        response.contains("密码修改成功") -> true
                        response.contains("密码错误") -> false

                        else -> error(response)
                    }
                }

                else -> error("ModifyPassword Failed (${it.status.value}: ${it.status.description})")
            }
        }
}

suspend fun modifyPassword(token: String, userId: String, oldPassword: String, newPassword: String) =
    ModifyPassword(token, userId, oldPassword, newPassword)