@file:Suppress("unused", "FunctionName")

package io.github.smfdrummer.utils.strategy.extensions

import io.github.smfdrummer.common.platformConfig
import io.github.smfdrummer.utils.strategy.StrategyBuilder

fun StrategyBuilder.V303(id: Int) = packet {
    i = "V303"
    retry = 2

    parse(
        """
        {
          "al": [
            {
              "id": $id,
              "abi": 0,
              "type": 1,
              "config_version": 1
            }
          ],
          "ci": "93",
          "cs": "0",
          "pack": "${platformConfig.channel.packageName}",
          "pi": "{{pi}}",
          "sk": "{{sk}}",
          "ui": "{{ui}}",
          "v": "${platformConfig.channel.version}"
        }
    """.trimIndent()
    )

    onFailure { false }
}