@file:Suppress("unused", "SpellCheckingInspection")

package io.github.smfdrummer.network

import io.github.smfdrummer.utils.json.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import network.TalkwebData
import network.TalkwebForm
import org.bouncycastle.crypto.engines.RijndaelEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.paddings.ZeroBytePadding
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.MessageDigest
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec

fun TalkwebData.encryptRequest(): TalkwebForm = Crypto.Request.encrypt(this)
fun TalkwebForm.decryptRequest(): TalkwebData = Crypto.Request.decrypt(this)

fun String.encryptResponse(): String = Crypto.Response.encrypt(this)
fun String.decryptResponse(): String = Crypto.Response.decrypt(this)

fun String.encryptGzip(): String = Crypto.Gzip.encrypt(this)
fun String.decryptGzip(): String = Crypto.Gzip.decrypt(this)

fun ByteArray.encryptBase64(): String = Crypto.Base64.encrypt(this)
fun String.decryptBase64(): ByteArray = Crypto.Base64.decrypt(this)

fun String.encryptTwPay(): String = Crypto.TwPay.encrypt(this)
fun String.decryptTwPay(): String = Crypto.TwPay.decrypt(this)

fun Int.encryptNumber(): Int = Crypto.Number.encrypt(this)
fun Int.decryptNumber(): Int = Crypto.Number.decrypt(this)

fun String.getKey(encryptVersion: Int = 2): ByteArray = Crypto.TwNetwork(this, encryptVersion).getKey()
fun String.getIv(encryptVersion: Int = 2): ByteArray = Crypto.TwNetwork(this, encryptVersion).getIv()
fun String.getMD5(): String = Crypto.getMD5(this)


internal object Crypto {
    const val MD5KEY = "talkwebCert"
    const val APPKEY = "b0b29851-b8a1-4df5-abcb-a8ea158bea20"

    internal object Request {
        fun encrypt(data: TalkwebData): TalkwebForm = TalkwebForm(
            req = data.req,
            e = TwNetwork(data.req, data.ev).encrypt(
                data.d.toJsonString(JsonFeature.ImplicitNulls).encodeToByteArray()
            ),
            ev = data.ev
        )

        fun decrypt(data: TalkwebForm): TalkwebData = TalkwebData(
            req = data.req,
            d = TwNetwork(data.req, data.ev).decrypt(data.e).decodeToString()
                .parseObject(JsonFeature.ImplicitNulls),
            ev = data.ev
        )
    }

    internal object Response {
        fun encrypt(data: String): String {
            val parse = data.parseObject()
            return buildJsonObject {
                put("i", parse.getString("i"))
                put("r", parse.getInt("r"))
                put("e", TwNetwork(parse.getString("i")!!).encrypt(data.encodeToByteArray()))
            }.toJsonString(JsonFeature.ImplicitNulls)
        }

        fun decrypt(data: String): String = try {
            val parse = data.parseObject()
            TwNetwork(parse.getString("i")!!).decrypt(parse.getString("e")!!).decodeToString()
        } catch (_: Exception) {
            data
        }
    }

    internal object Gzip {
        private fun String.addQuotes(): String = "\"$this\""
        private fun String.subQuotes(): String = this.removeSurrounding("\"")

        fun encrypt(data: String): String = Base64.encrypt(
            ByteArrayOutputStream().use { outputStream ->
                GZIPOutputStream(BufferedOutputStream(outputStream)).use { gzipStream ->
                    gzipStream.write(Base64.encrypt(data.encodeToByteArray()).addQuotes().encodeToByteArray())
                }
                outputStream.toByteArray()
            }
        )

        fun decrypt(data: String): String = Base64.decrypt(
            ByteArrayOutputStream().use { outputStream ->
                BufferedInputStream(GZIPInputStream(ByteArrayInputStream(Base64.decrypt(data)))).use { bufferedGzipInput ->
                    val buffer = ByteArray(8192)
                    var len = bufferedGzipInput.read(buffer)
                    while (len != -1) {
                        outputStream.write(buffer, 0, len)
                        len = bufferedGzipInput.read(buffer)
                    }
                }
                outputStream.toString(Charsets.UTF_8).subQuotes()
            }
        ).decodeToString()
    }

    internal object Base64 {
        fun encrypt(data: ByteArray): String = java.util.Base64.getUrlEncoder().encodeToString(data).replace("=", ",")
        fun decrypt(data: String): ByteArray = java.util.Base64.getUrlDecoder().decode(data.replace(',', '='))
    }

    internal object TwPay {
        private val HEX = "0123456789ABCDEF".toCharArray()
        private val IV_PARAMETER = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
        private val cipher: (Int) -> Cipher = {
            Cipher.getInstance("DES/CBC/PKCS5Padding").apply {
                init(it, generateKey("TwPay001"), IvParameterSpec(IV_PARAMETER))
            }
        }

        private fun generateKey(@Suppress("SameParameterValue") password: String): Key {
            val dks = DESKeySpec(password.encodeToByteArray())
            val keyFactory = SecretKeyFactory.getInstance("DES")
            return keyFactory.generateSecret(dks)
        }

        fun encrypt(data: String): String = buildString {
            cipher(Cipher.ENCRYPT_MODE).doFinal(data.encodeToByteArray()).forEach {
                append(HEX[it.toInt() and 0xf0 shr 4])
                append(HEX[it.toInt() and 0xf])
            }
        }

        fun decrypt(data: String): String = cipher(Cipher.DECRYPT_MODE).doFinal(
            data.uppercase(Locale.getDefault())
                .chunked(2)
                .map {
                    val m = it[0].code - if (it[0] > '9') 55 else 48
                    val n = it[1].code - if (it[1] > '9') 55 else 48
                    ((m shl 4) + n).toByte()
                }
                .toByteArray()
        ).decodeToString()
    }

    internal object Number {
        fun encrypt(data: Int): Int = ((data xor 13) shl 13) or (data ushr 19)
        fun decrypt(data: Int): Int = ((data ushr 13) xor 13) or (data shl 19)
    }

    internal class TwNetwork(private val identifier: String, private val encryptVersion: Int = 2) {
        private val fixPairs = arrayOf(
            "" to "", // 0 -> PLACEHOLDER
            "YGpvdSo," to "KXhvaic,", // 1 -> OLD
            "VnhTOVJtN2Y," to "OGdLVF82Mzg," // 2 -> NEW
        )

        private fun getFixPair() = fixPairs.getOrNull(encryptVersion)?.let { (prefix, suffix) ->
            prefix.decryptBase64().decodeToString() to suffix.decryptBase64().decodeToString()
        } ?: error("Unsupported encrypt version: $encryptVersion")

        private fun rijndael(data: ByteArray, key: ByteArray, iv: ByteArray, forEncryption: Boolean): ByteArray {
            val rijndael = RijndaelEngine(192)
            val zeroBytePadding = ZeroBytePadding()
            val parametersWithIV = ParametersWithIV(KeyParameter(key), iv)
            val cipher = PaddedBufferedBlockCipher(CBCBlockCipher.newInstance(rijndael), zeroBytePadding)
            cipher.init(forEncryption, parametersWithIV)
            return cipher(cipher, data)
        }

        private fun cipher(cipher: PaddedBufferedBlockCipher, data: ByteArray): ByteArray {
            val outputSize = cipher.getOutputSize(data.size)
            val output = ByteArray(outputSize)
            val length = cipher.processBytes(data, 0, data.size, output, 0)
            val finalLength = length + cipher.doFinal(output, length)
            return output.copyOf(finalLength)
        }

        fun getKey(): ByteArray = getFixPair().let { (prefix, suffix) ->
            getMD5("$prefix$identifier$suffix").encodeToByteArray()
        }

        fun getIv(): ByteArray {
            val key = getKey().decodeToString()
            val pos = identifier.filter { it.isDigit() }.toIntOrNull() ?: 0
            val start = pos % 7
            val end = (start + 24).coerceAtMost(key.length)
            return key.substring(start, end).encodeToByteArray()
        }

        fun encrypt(data: ByteArray): String =
            Base64.encrypt(rijndael(data, getKey(), getIv(), true))

        fun decrypt(data: String): ByteArray =
            rijndael(Base64.decrypt(data), getKey(), getIv(), false)
    }

    internal fun getMD5(toBeHashed: String): String =
        MessageDigest.getInstance("MD5").digest(toBeHashed.encodeToByteArray()).joinToString("") { "%02x".format(it) }
}