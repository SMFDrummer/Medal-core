package io.github.smfdrummer.network

import io.github.smfdrummer.enums.GameHost
import io.github.smfdrummer.utils.json.JsonFeature
import io.github.smfdrummer.utils.json.jsonWith
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

object Requester {
    @Volatile
    var interval = 350.milliseconds

    @Volatile
    var logger: (String) -> Unit = { println(it) }

    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    this@Requester.logger(message)
                }
            }
            level = LogLevel.BODY
        }
        install(ContentNegotiation) {
            json(
                jsonWith(
                    JsonFeature.IgnoreUnknownKeys,
                    JsonFeature.IsLenient
                )
            )
        }
    }

    private suspend fun waiting() = delay(interval)

    private suspend fun Pair<String, String>.build(host: GameHost): HttpResponse = client.post(host.url) {
        header(HttpHeaders.ContentType, ContentType.MultiPart.FormData)
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("req", this@build.first)
                    append("e", this@build.second)
                    append("ev", CryptoDefaults.cryptoType)
                }
            )
        )
    }

    suspend fun Pair<String, String>.request(host: GameHost): String = withContext(Dispatchers.IO) {
        try {
            waiting()
            build(host).let {
                when (it.status.value) {
                    200 -> it.bodyAsText()
                    else -> throw RequestException("Failed to request to ${host.url} (${it.status.value}): ${it.status.description}")
                }
            }
        } catch (e: Exception) {
            when (e) {
                is RequestException -> throw e
                else -> throw RequestException("Unexpected error during request to ${host.url}", e)
            }
        }
    }

    suspend fun post(urlString: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
        withContext(Dispatchers.IO) {
            waiting()
            client.post(urlString, block)
        }
}

class RequestException(message: String, cause: Throwable? = null) : Exception(message, cause)