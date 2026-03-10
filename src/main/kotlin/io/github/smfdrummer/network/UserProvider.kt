package io.github.smfdrummer.network

interface UserProvider {
    /**
     * @return the first is userId and the second is token
     */
    suspend fun fetch(): Pair<String, String>
}