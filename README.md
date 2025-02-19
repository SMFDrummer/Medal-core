<p align="center">
    <img src="https://raw.githubusercontent.com/SMFDrummer/Medal-core/refs/heads/main/assets/medal-core.webp" alt="logo" height="128" width="128">
</p>

---------

<h2 align="center">
    Medal-core
</h2>

<h5 align="center">English | <a href="https://github.com/SMFDrummer/Medal-core/blob/main/README.zh.md">简体中文</a></h5>

<h3 align="center">
Medal's core library, the ultimate tool for packet delivery
</h3>
<div align="center">

Medal-core is fully developed by Kotlin, compatible with Kotlin Multiplatform, with rich extension functions and
Kotlin-DSL, supports flexible policy configuration, and is compatible with IOS/Android and packet delivery of various
channels.

![Kotlin version](https://img.shields.io/static/v1?label=Kotlin&message=2.1.0&color=blue)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.smfdrummer/medal-core)
![GitHub License](https://img.shields.io/github/license/SMFDrummer/Medal-core)

</div>

--------

# Introduction

Medal-core is a unified core library for packet DSL, build, modify, delivery, and post-processing, and completes all
processing of packets in the fastest and most efficient way.

* **PacketBuiltIn**：The core library has a built-in structure for most of the packets, allowing for rapid packet
  building
* **Self-implementation policy configuration resolution**：Custom JSON policy configuration, Kotlin-DSL configuration,
  and dynamic policy configuration are supported
* **MultiPlatformSupport**：It supports IOS/Android and data packet delivery from various channels, which is easy to set
  up and easy to use
* **AbundantExtensionFunctions**：A large number of extension functions are supported to facilitate the rapid
  construction of data packets
* **BuiltInSQLite**：In the future, built-in SQLite will be supported to support the construction and operation of user
  databases

--------

# SimpleExample

Here's a simple example of using Medal-core, showing how to use Medal-core to quickly process packets

```kotlin
suspend fun main() {
    latestVersion = getLatestVersion()
    platformManager.switchToAndroid(Channel.Official)

    val user = User(
        userId = primitive { 12345678 },
        password = "medal-test-password"
    )
    
    buildStrategy {
        version = 1
        description = "Android - Get user credentials"

        packet {
            i = "V202"
            retry = 4
            ext("sk") { "sk" }
            ext("ui") { "ui" }
            onSuccess { true }
        }
    }.execute(user) {
        println("Response: ${responses["V202"]}")
        println("Val sk: ${variables["sk"]}")
        println("Val ui: ${variables["ui"]}")
    }
}
```

```kotlin
suspend fun main() {
    latestVersion = getLatestVersion()
    platformManager.switchToAndroid(Channel.Official)

    val user = User(
        userId = primitive { 12345678 },
        password = "medal-test-password"
    )
    
    """
        {
            "version": 1,
            "description": "Android - Get user credentials",
            "packets": [
                {
                    "i": "V202",
                    "r": 0,
                    "t": {},
                    "repeat": 1,
                    "retry": 4,
                    "extract": {
                        "sk": "d.sk",
                        "ui": "d.ui"
                    },
                    "onSuccess": true
                }
            ]
        }
    """.trimIndent().to<StrategyConfig>().execute(user) {
        println("Response: ${responses["V202"]}")
        println("Val sk: ${variables["sk"]}")
        println("Val ui: ${variables["ui"]}")
    }
}
```

The above two methods are completely equivalent, and both use the Medal-core method to construct packets, execute
packets, and process packets

## OperatingEnvironment

- **JDK 21 - jvm**、**JDK 17 - android**
- **Kotlin 2.1.0**
- **Maven 3.6.3+** or **Gradle 8.10.2**

> Make sure that the kotlin plugin for your IDE supports kotlin 2.1.0 or later
>
> Incompatible **Gradle 8.12** or above
>
> If you build in **Intellij IDEA** with an error，or build with **Maven**、
> try enabling the following settings: **Settings / Build, Execution, Deployment / Build Tools / Maven / Runner /
Delegate IDE build/run actions to Maven**.

--------

## Installation

### Gradle(kts)

<summary>

<details>

Make sure your project root of `settings.gradle.kts` is added Maven Central repository

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public") // Optional for China Mainland users
    }
}
```

Then add dependency in `build.gradle.kts`

```kotlin
dependencies {
    implementation("io.github.smfdrummer:medal-core:${latest.version}")
}
```

Including latest.version is the latest version number，you can find it
in [Maven Central](https://central.sonatype.com/artifact/io.github.smfdrummer/medal-core)

</details>

</summary>

### Gradle(groovy)

<summary>

<details>

Make sure your project root of `settings.gradle` is added Maven Central repository

```groovy
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url 'https://maven.aliyun.com/repository/public' } // Optional for China Mainland users
    }
}

```

Then add dependency in `build.gradle`

```groovy
dependencies {
    implementation 'io.github.smfdrummer:medal-core:{{latest.version}}'
}
```

Including latest.version is the latest version number，you can find it
in [Maven Central](https://central.sonatype.com/artifact/io.github.smfdrummer/medal-core)

</details>

</summary>

### Maven

<summary>

<details>

Add the following to the `pom.xml` file:

1. Add Maven Central repository (If it is not included by default):

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <!-- Aliyun public Maven repository for China Mainland users -->
    <repository>
        <id>aliyun-central</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

2. Add medal-core dependency：

```xml
<dependencies>
    <dependency>
        <groupId>io.github.smfdrummer</groupId>
        <artifactId>medal-core</artifactId>
        <version>latest.version</version>
    </dependency>
</dependencies>
```

Including latest.version is the latest version number，you can find it
in [Maven Central](https://central.sonatype.com/artifact/io.github.smfdrummer/medal-core)

</details>

</summary>

--------

## Usage

For more information, please visit: [Wiki](https://github.com/SMFDrummer/Medal-core/wiki) learn how to use it

--------

## License

Medal-core is released under the [AGPL 3.0](https://www.gnu.org/licenses/agpl-3.0.html) license

> Unless commercially licensed, the code needs to be open source in any way you modify or use it
>
> Only projects for personal use and open source can use this library, otherwise there will be a fee
>
> Please comply with the license regulations and do not violate the license regulations, otherwise you will be held
> legally responsible

--------

## Acknowledgment

Thank you for using Medal-core, and if you have any questions or suggestions, please
take [Issues](https://github.com/SMFDrummer/Medal-core/issues)

Projects cite the following open source projects (in no particular order):

- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Kotlinx Serialization JsonPath](https://github.com/nomisRev/kotlinx-serialization-jsonpath)
- [Arrow-kt](https://github.com/arrow-kt/arrow)
- [Ktor](https://github.com/ktorio/ktor)
- [Logback](https://github.com/qos-ch/logback) & [Logback Android](https://github.com/tony19/logback-android)
- [Bouncy Castle](https://www.bouncycastle.org)
- [Reflections](https://github.com/ronmamo/reflections)
- [Kronos-orm](https://github.com/Kronos-orm/Kronos-orm)

--------

## Sponsored

If you like Medal-core and want to support the development of the project, you can sponsor in the following
ways: [AiFaDian](https://afdian.com/a/smfdrummer)

--------

#### This project uses [Intellij IDEA](https://jetbrains.com/idea) to build and publish

#### If you like this project, please give a star ⭐️. Thank you for your support