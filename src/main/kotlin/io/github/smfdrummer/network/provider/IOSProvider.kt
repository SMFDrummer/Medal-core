package io.github.smfdrummer.network.provider

import io.github.smfdrummer.network.UserProvider

class IOSProvider(private val udid: String) : UserProvider {
    override suspend fun fetch(): Pair<String, String> = udid to ""
}