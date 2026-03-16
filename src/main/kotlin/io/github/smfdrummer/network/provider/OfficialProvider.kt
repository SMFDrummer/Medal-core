package io.github.smfdrummer.network.provider

import io.github.smfdrummer.network.UserProvider
import io.github.smfdrummer.network.service.loginNew

class OfficialProvider(
    private val phoneOrUserId: String,
    private val password: String,
) : UserProvider {
    override suspend fun fetch(): Pair<String, String> = loginNew(phoneOrUserId, password)
}