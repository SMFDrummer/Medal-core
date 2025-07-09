@file:Suppress("unused", "FunctionName")

package io.github.smfdrummer.utils.strategy.extensions

import io.github.smfdrummer.common.platformConfig
import io.github.smfdrummer.network.Crypto.getMD5
import io.github.smfdrummer.utils.strategy.StrategyBuilder

fun StrategyBuilder.V201(udid: String) = packet {
    i = "V201"
    retry = 4

    val di = getMD5(udid)
    val random = (1000000000..2000000000).random()
    parse(
        """
        {
          "cv": "${platformConfig.channel.version}",
          "di": "$di",
          "r": "$random",
          "s": "${getMD5(di + random + "B7108D8B5TABE")}"
        }
        """.trimIndent()
    )

    response(
        """
        
    """.trimIndent()
    )

    extract {
        "ui" from "ui"
        "sk" from "sk"
    }
}