package network

import io.github.edmondantes.multipart.serialization.MultipartForm
import io.github.edmondantes.multipart.serialization.config.MultipartFormConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.JsonObject
import utils.json.JsonSerializable

@Serializable
data class TalkwebForm(
    val req: String,
    val e: String,
    val ev: Int
) : JsonSerializable

@Serializable
data class TalkwebData(
    val req: String,
    val d: JsonObject,
    val ev: Int
) : JsonSerializable

fun String.parseToTalkwebForm() = Former.decode(trim().plus("\n").encodeToByteArray())
fun TalkwebForm.encodeToString() = Former.encode(this).decodeToString()

internal object Former {
    const val BOUNDARY = "_{{}}_"
    private val config = MultipartFormConfig(BOUNDARY)
    private val form = MultipartForm(config)

    internal fun decode(bytes: ByteArray) = form.decodeFromByteArray<TalkwebForm>(bytes)
    internal fun encode(data: TalkwebForm) = form.encodeToByteArray(data)
}