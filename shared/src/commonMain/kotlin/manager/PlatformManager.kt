package manager

import api.Channel
import config.AndroidConfig
import config.IOSConfig
import config.PlatformConfig

val platformManager = PlatformManager()

val config: PlatformConfig
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