package io.github.smfdrummer.network.service

import io.github.smfdrummer.enums.TalkwebHost
import io.github.smfdrummer.network.Requester
import io.github.smfdrummer.network.model.LatestVersions
import io.github.smfdrummer.utils.json.fromJson
import io.ktor.client.statement.bodyAsText

internal object GetLatestVersion {
    internal suspend operator fun invoke(): String = Requester.post(TalkwebHost.GET_LATEST_VERSION.url).bodyAsText()
}

suspend fun getLatestVersion() = GetLatestVersion().fromJson<LatestVersions>().latestVersions.associateBy { it.channel }