package service

import api.TalkwebHost
import io.ktor.client.request.*
import io.ktor.client.statement.*
import packet.client
import service.model.LatestVersions
import to

object GetLatestVersion {
    suspend operator fun invoke(): String = client.post(TalkwebHost.GET_LATEST_VERSION.url).bodyAsText()
}

suspend fun getLatestVersion() = GetLatestVersion().to<LatestVersions>().latestVersions.associateBy { it.channel }

lateinit var latestVersion: Map<String, LatestVersions.LatestVersion>