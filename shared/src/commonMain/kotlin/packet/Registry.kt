package packet

import org.reflections.Reflections
import service.model.User
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

lateinit var packetsPackage: String

object Registry {
    private val packetConstructors: Map<String, KFunction<Packet>> by lazy {
        val reflections = Reflections(packetsPackage)
        val subTypes = reflections.getSubTypesOf(Packet::class.java)
        subTypes.associate { subType ->
            val kClass = subType.kotlin
            val identifier = kClass.annotations
                .filterIsInstance<PacketIdentifier>()
                .firstOrNull()?.value
                ?: throw IllegalStateException("Cannot retrieve identifier for $subType")
            identifier to (kClass.primaryConstructor
                ?: throw IllegalStateException("No primary constructor for $subType"))
        }
    }

    fun getPacket(identifier: String, user: User): Packet? = packetConstructors[identifier]?.call(user)

    fun getAllPackets(user: User): Map<String, Packet> =
        packetConstructors.mapValues { (_, constructor) -> constructor.call(user) }
}