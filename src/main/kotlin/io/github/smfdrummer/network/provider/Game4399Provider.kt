package io.github.smfdrummer.network.provider

import io.github.smfdrummer.enums.Game4399Host
import io.github.smfdrummer.network.Requester
import io.github.smfdrummer.network.UserProvider
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class Game4399Provider(
    private val username: String,
    private val password: String,
) : UserProvider {
    suspend fun getState(): Pair<String, String> = Requester.post(Game4399Host.STATE.url).let {
        when (it.status.value) {
            200 -> {
                val payload = Json.parseToJsonElement(it.bodyAsText()).jsonObject
                val code = payload["code"]?.jsonPrimitive?.content
                if (code != "98") {
                    error("Login Failed ($code: ${payload["message"]?.jsonPrimitive?.content ?: "Unknown"})")
                }

                val resultUrl = payload["result"]?.jsonPrimitive?.content
                    ?: error("Login Failed: missing result url")

                val clientId = extractQueryParam(resultUrl, "client_id")
                    ?: error("Login Failed: missing client_id in result url")
                val state = extractQueryParam(resultUrl, "state")
                    ?: error("Login Failed: missing state in result url")

                clientId to state
            }

            else -> error("Login Failed (${it.status.value}: ${it.status.description})")
        }
    }


    override suspend fun fetch(): Pair<String, String> {
        val (clientId, state) = getState()
        suspend fun HttpResponse.fetchData(): Pair<String, String> {
            val payload = Json.parseToJsonElement(bodyAsText()).jsonObject
            val code = payload["code"]?.jsonPrimitive?.content
            if (code != "100") {
                error("Login Failed ($code: ${payload["message"]?.jsonPrimitive?.content ?: "Unknown"})")
            }

            val result = payload["result"]?.jsonObject
                ?: error("Login Failed: missing result")
            val uid = result["uid"]?.jsonPrimitive?.content
                ?: error("Login Failed: missing uid")
            val userState = result["state"]?.jsonPrimitive?.content
                ?: error("Login Failed: missing state")

            return uid to userState
        }
        return Requester.post(Game4399Host.LOGIN.url) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        listOf(
                            "password" to password,
                            "username" to username,
                            "client_id" to clientId,
                            "redirect_uri" to Game4399Host.STATE.url,
                            "response_type" to "TOKEN",
                            "state" to state
                        ).forEach { (key, value) -> append(key, value) }
                    }
                )
            )
        }.let {
            when (it.status.value) {
                200 -> it.fetchData()
                302 -> {
                    val location = it.headers[HttpHeaders.Location]
                        ?: error("Login Failed: 302 missing location")
                    Requester.post(location).let { res ->
                        when (res.status.value) {
                            200 -> res.fetchData()

                            else -> error("Login Failed (${res.status.value}: ${res.status.description})")
                        }
                    }
                }

                else -> error("Login Failed (${it.status.value}: ${it.status.description})")
            }
        }
    }

    private fun extractQueryParam(url: String, key: String): String? {
        val query = URI(url).rawQuery ?: return null
        return query
            .split("&")
            .asSequence()
            .mapNotNull { part ->
                val index = part.indexOf('=')
                if (index < 0) return@mapNotNull null
                val paramKey = URLDecoder.decode(part.substring(0, index), StandardCharsets.UTF_8)
                if (paramKey != key) return@mapNotNull null
                URLDecoder.decode(part.substring(index + 1), StandardCharsets.UTF_8)
            }
            .firstOrNull()
    }
}
