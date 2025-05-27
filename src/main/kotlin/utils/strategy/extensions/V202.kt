@file:Suppress("unused", "FunctionName")

package io.github.smfdrummer.utils.strategy.extensions

import io.github.smfdrummer.common.platformConfig
import io.github.smfdrummer.utils.strategy.StrategyBuilder

fun StrategyBuilder.V202(userId: String? = null, token: String? = null) = packet {
    i = "V202"
    retry = 4

    if (userId != null && token != null) {
        parse(
            """
          {
            "pi": "",
            "ui": "",
            "ci": "93",
            "cv": "${platformConfig.channel.version}",
            "di": "",
            "head": {
              "appId": "${platformConfig.channel.appId}",
              "appVersion": "1.0",
              "channelId": "${platformConfig.channel.channelId}",
              "channelSdkVersion": "dj2.0-2.0.0",
              "talkwebSdkVersion": "3.0.0"
            },
            "li": "84b8aa5c65c0d32b4f8ac2f5f0c0592f",
            "oi": "${with(platformConfig.channel) { "$appId$channelId" }}X$userId",
            "r": "1600340008",
            "s": "0e9788be5612edcaa9d03349f3cdf707",
            "t": "$token"
          }
        """.trimIndent()
        )

        extract {
            "pi" from "ui"
            "sk" from "sk"
            "ui" from "ui"
        }
    } else {
        parse(
            """
          {
            "sk": "{{sk}}",
            "ui": "{{ui}}"
          }
        """.trimIndent()
        )

        extract {
            "pi" from "pi"
        }
    }

    onSuccess { true }
}