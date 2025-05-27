package io.github.smfdrummer.utils.strategy.applications

import io.github.smfdrummer.utils.strategy.buildStrategy
import io.github.smfdrummer.utils.strategy.extensions.V201
import io.github.smfdrummer.utils.strategy.extensions.V202
import io.github.smfdrummer.utils.strategy.extensions.V203

fun androidCredential(userId: String, token: String) = buildStrategy {
    version = 1
    description = "Android - 获取用户凭据"

    V202(userId, token)
}

fun iosCredential(udid: String, isRandom: Boolean) = buildStrategy {
    version = 1
    description = "IOS - 获取用户凭据"

    if (isRandom) {
        V201(udid)
        V203()
    } else {
        V201(udid)
        V202()
    }
}
