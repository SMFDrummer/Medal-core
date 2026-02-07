package io.github.smfdrummer.network

import io.github.smfdrummer.enums.GameHost
import io.github.smfdrummer.utils.json.JsonFeature
import io.github.smfdrummer.utils.json.jsonWith
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import network.Former
import network.TalkwebForm
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Requester {
    // 基础请求间隔（正常场景）
    @Volatile
    var baseInterval: Duration = 350.milliseconds

    // 错误退避间隔（403/5xx等异常场景）
    @Volatile
    var errorBackoffInterval: Duration = 3000.milliseconds

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
        // 可选：添加超时配置，避免请求挂起太久
        engine {
            requestTimeout = 10000.milliseconds.inWholeMilliseconds
        }
    }

    /**
     * 统一的延迟执行逻辑，区分正常/错误场景
     */
    private suspend fun applyDelay(isError: Boolean = false) {
        val delayTime = if (isError) errorBackoffInterval else baseInterval
        logger("Applying delay: ${delayTime.inWholeMilliseconds}ms (error: $isError)")
        delay(delayTime)
    }

    private suspend fun TalkwebForm.build(host: GameHost): HttpResponse {
        return client.post(host.url) {
            header(HttpHeaders.ContentType, ContentType.MultiPart.FormData.withParameter("boundary", Former.BOUNDARY))
            setBody(Former.encode(this@build))
        }
    }

    /**
     * 修复后的TalkwebForm请求方法：确保延迟必执行，错误场景加长间隔
     */
    suspend fun TalkwebForm.request(host: GameHost): String = withContext(Dispatchers.IO) {
        var isError = false
        try {
            // 先执行延迟（避免首次请求无间隔）
            applyDelay()

            val response = build(host)
            when (response.status.value) {
                200 -> response.bodyAsText()
                else -> {
                    isError = true
                    throw RequestException("Failed to request to ${host.url} (${response.status.value}): ${response.status.description}")
                }
            }
        } catch (e: Exception) {
            isError = true
            val errorMsg = when (e) {
                is RequestException -> e.message ?: "Unknown request error"
                else -> "Unexpected error during request to ${host.url}"
            }
            logger("Request failed: $errorMsg")
            throw RequestException(errorMsg, e)
        } finally {
            // 关键：无论成功/失败，都执行延迟（错误场景用退避间隔）
            // 这里的延迟是为下一次请求做准备
            applyDelay(isError)
        }
    }

    /**
     * 通用post方法：同样保证延迟必执行
     */
    suspend fun post(urlString: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
        withContext(Dispatchers.IO) {
            var isError = false
            try {
                // 基础延迟（可替换为baseInterval）
                applyDelay()

                val response = client.post(urlString, block)
                // 检测响应状态，标记错误
                if (response.status.value !in 200..299) {
                    isError = true
                }
                return@withContext response
            } catch (e: Exception) {
                isError = true
                logger("Generic post failed to $urlString: ${e.message}")
                throw RequestException("Failed to post to $urlString", e)
            } finally {
                // 错误场景用退避间隔
                applyDelay(isError)
            }
        }
}

class RequestException(message: String, cause: Throwable? = null) : Exception(message, cause)
