package packet

import kotlinx.serialization.json.JsonObject
import service.model.User

abstract class Packet {
    abstract val identifier: String
    abstract val user: User
    abstract fun build(): Pair<String, JsonObject>
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class PacketIdentifier(val value: String)