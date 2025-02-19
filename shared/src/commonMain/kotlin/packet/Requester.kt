package packet

import api.GameHost
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

val client = HttpClient(CIO)

private suspend fun Pair<String, String>.build(host: GameHost): HttpResponse = client.post(host.url) {
    header(HttpHeaders.ContentType, ContentType.MultiPart.FormData)
    setBody(
        MultiPartFormDataContent(
            formData {
                append("req", this@build.first)
                append("e", this@build.second)
                append("ev", 1)
            }
        )
    )
}

suspend fun Pair<String, String>.request(host: GameHost): String = this.build(host).let {
    delay(350.milliseconds)
    return@let when (it.status.value) {
        200 -> it.bodyAsText()
        else -> error("Failed to request (${it.status.value}): ${it.status.description}")
    }
}
