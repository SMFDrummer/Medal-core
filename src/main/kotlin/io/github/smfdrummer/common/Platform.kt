package io.github.smfdrummer.common

import io.github.smfdrummer.enums.Channel
import io.github.smfdrummer.enums.GameHost

interface PlatformConfig {
    val salt: String
    var channel: Channel
    val host: GameHost
}

data object AndroidConfig : PlatformConfig {
    override val salt = "android"
    override var channel = Channel.Official
    override val host = GameHost.ANDROID
}

data object IOSConfig : PlatformConfig {
    override val salt = "ios"
    override var channel = Channel.IOS
    override val host = GameHost.IOS
}

val platformManager = PlatformManager()

val platformConfig: PlatformConfig
    get() = platformManager.getConfig()

class PlatformManager {
    private lateinit var currentConfig: PlatformConfig

    fun switchToAndroid(channel: Channel) {
        currentConfig = AndroidConfig.apply { this.channel = channel }
    }

    fun switchToIOS() {
        currentConfig = IOSConfig
    }

    fun getConfig(): PlatformConfig = currentConfig
}