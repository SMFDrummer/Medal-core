package service.model

import config.AndroidConfig
import config.IOSConfig
import getIntValue
import getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import manager.config
import manager.execute
import packet.Packet
import packet.Registry
import packet.model.Credential
import strategy.androidCredential
import strategy.iosCredentialNonRandom
import strategy.iosCredentialRandom

@Serializable
data class User(
    @Required val userId: JsonPrimitive,
    val userNick: String? = null,
    val token: String? = null,
    val phone: String? = null,
    @SerialName("credential") var _credential: Credential? = null,
    var password: String? = null,
    var activate: Boolean = true,
    var banned: Boolean = false
) {
    @Transient
    val scope = CoroutineScope(Dispatchers.IO)

    @Transient
    val lock = Mutex()

    @Transient
    var uk = 1001
        get() = ++field

    fun refreshCredential(isRandom: Boolean = false) = scope.launch {
        when (config) {
            AndroidConfig -> androidCredential().execute(this@User) {
                val r = responses["V202"]?.getIntValue("r")
                val d = responses["V202"]?.getString("d")
                when (r) {
                    0 -> {
                        _credential = Credential(
                            personalId = variables["ui"]!!.jsonPrimitive.content,
                            securityKey = variables["sk"]!!.jsonPrimitive.content,
                            userId = variables["ui"]!!.jsonPrimitive.content
                        )
                    }

                    20507 -> throw UserBannedException(d)
                    else -> error("获取用户凭据失败 ($r): $d")
                }
            }

            IOSConfig -> {
                when (isRandom) {
                    true -> iosCredentialRandom()
                    false -> iosCredentialNonRandom()
                }.execute(this@User) {
                    val response = if (isRandom) responses["V203"] else responses["V202"]
                    val r = response?.getIntValue("r")
                    val d = response?.getString("d")
                    when (r) {
                        0 -> {
                            _credential = Credential(
                                personalId = variables["pi"]!!.jsonPrimitive.content,
                                securityKey = variables["sk"]!!.jsonPrimitive.content,
                                userId = variables["ui"]!!.jsonPrimitive.content
                            )
                        }

                        20507 -> throw UserBannedException(d)
                        else -> error("获取用户凭据失败 ($r): $d")
                    }
                }
            }
        }
    }

    fun shutdown(message: String) {
        scope.cancel(message)
    }
}

val User.credential: Credential
    get() = requireNotNull(this._credential) { "用户凭据未初始化" }

val User.packets: Map<String, Packet?>
    get() = object : Map<String, Packet?> {
        override val entries: Set<Map.Entry<String, Packet?>>
            get() = Registry.getAllPackets(this@packets).entries
        override val keys: Set<String>
            get() = Registry.getAllPackets(this@packets).keys
        override val size: Int
            get() = Registry.getAllPackets(this@packets).size
        override val values: Collection<Packet?>
            get() = Registry.getAllPackets(this@packets).values

        override fun containsKey(key: String): Boolean = Registry.getPacket(key, this@packets) != null
        override fun containsValue(value: Packet?): Boolean = Registry.getAllPackets(this@packets).containsValue(value)
        override fun get(key: String): Packet? = Registry.getPacket(key, this@packets)
        override fun isEmpty(): Boolean = Registry.getAllPackets(this@packets).isEmpty()
    }

class UserBannedException(override val message: String?) : Exception(message)