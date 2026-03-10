package io.github.smfdrummer.network.provider

import io.github.smfdrummer.network.UserProvider
import io.github.smfdrummer.network.service.login

class OfficialProvider(
    private val phoneOrUserId: String,
    private val passwordMD5: String,
) : UserProvider {
    override suspend fun fetch(): Pair<String, String> = login(phoneOrUserId, passwordMD5)
}