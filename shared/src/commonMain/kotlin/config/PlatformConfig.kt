package config

import api.Channel
import api.GameHost

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