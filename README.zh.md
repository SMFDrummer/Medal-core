<p align="center">
    <img src="https://raw.githubusercontent.com/SMFDrummer/Medal-core/refs/heads/main/assets/medal-core.webp" alt="logo" height="128" width="128">
</p>

---------

<h2 align="center">
    Medal-core
</h2>

<h5 align="center"><a href="https://github.com/SMFDrummer/Medal-core/blob/main/README.md">English</a> | 简体中文</h5>

<h3 align="center">
Medal 的核心库，数据包投递最终利器
</h3>
<div align="center">

Medal-core 完全使用 Kotlin 开发，兼容 Kotlin Multiplatform，具有丰富的扩展函数以及 Kotlin-DSL，支持灵活的策略配置，兼容
IOS/Android 及各种渠道的数据包投递，**跨时代的最终利器**

![Kotlin version](https://img.shields.io/static/v1?label=Kotlin&message=2.1.0&color=blue)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.smfdrummer/medal-core)
![GitHub License](https://img.shields.io/github/license/SMFDrummer/Medal-core)

</div>

--------

# 介绍

Medal-core 是数据包集 DSL、构建、修改、投递与后处理的统一核心库，以最快最有效的方式完成对数据包的一切处理。

* **数据包内置**：核心库内置了大部分数据包的结构，支持快速构建数据包
* **自实现策略配置解析**：支持自定义 Json 策略配置，支持使用 Kotlin-DSL 进行配置，支持动态策略配置
* **多平台支持**：支持 IOS/Android 及各种渠道的数据包投递，设置方便，使用简单
* **丰富的扩展函数**：支持大量扩展函数，方便快速构建数据包

--------

# 简单示例

下面是一个使用 Medal-core 的简单示例，展示了如何使用 Medal-core 快速处理数据包

```kotlin
suspend fun main() {
    Channal.Official.version = "x.x.x"
    platformManager.switchToAndroid(Channel.Official)

    val user = User(
        userId = primitive { 12345678 },
        password = "medal-test-password"
    )

    buildStrategy {
        version = 1
        description = "Android - 获取存档信息"

        packet {
            i = "V316"

            parse("""
                {
                    "pi": "{{pi}}",
                    "sk": "{{sk}}",
                    "ui": "{{ui}}"
                }
            """.trimIndent())

            onSuccess { true }
        }
    }.executeWith(
        OfficialProvider(user.userId.content, user.password.getMD5())
    )
}
```

以上方式均使用 Medal-core 的方式构建数据包，执行数据包，处理数据包

## 运行环境

- **JDK 21 - jvm**、**JDK 17 - android**
- **Kotlin 2.1.0**
- **Maven 3.6.3+** or **Gradle 8.10.2**

> 请确保用于 IDE 的 kotlin 插件支持 kotlin 2.1.0 或更高版本
>
> 不兼容 **Gradle 8.12** 或更高版本
>
> 如果您在 **Intellij IDEA** 中构建失败，并使用 **Maven** 构建、
> 请尝试启用以下设置: **Settings / Build, Execution, Deployment / Build Tools / Maven / Runner / Delegate IDE build/run
actions to Maven**.

--------

## 安装

### Gradle(kts)

<summary>

<details>

请确保 project root 下的 `settings.gradle.kts` 文件中添加了 Maven Central 仓库

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public") // 国内用户可选
    }
}
```

然后在 `build.gradle.kts` 文件中添加以下依赖

```kotlin
dependencies {
    implementation("io.github.smfdrummer:medal-core:${latest.version}")
}
```

其中 latest.version
为最新版本号，可以在 [Maven Central](https://central.sonatype.com/artifact/io.github.smfdrummer/medal-core) 上查看

</details>

</summary>

### Gradle(groovy)

<summary>

<details>

请确保 project root 下的 `settings.gradle` 文件中添加了 Maven Central 仓库

```groovy
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url 'https://maven.aliyun.com/repository/public' } // 国内用户可选
    }
}

```

然后在 `build.gradle` 文件中添加以下依赖

```groovy
dependencies {
    implementation 'io.github.smfdrummer:medal-core:{{latest.version}}'
}
```

其中 latest.version
为最新版本号，可以在 [Maven Central](https://central.sonatype.com/artifact/io.github.smfdrummer/medal-core) 上查看

</details>

</summary>

### Maven

<summary>

<details>

在 `pom.xml` 文件中添加以下内容：

1. 添加 Maven Central 仓库（如果未默认包含）：

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <!-- 阿里云 Maven 仓库，供国内用户使用 -->
    <repository>
        <id>aliyun-central</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

2. 添加 medal-core 依赖：

```xml
<dependencies>
    <dependency>
        <groupId>io.github.smfdrummer</groupId>
        <artifactId>medal-core</artifactId>
        <version>latest.version</version>
    </dependency>
</dependencies>
```

其中 latest.version
为最新版本号，可以在 [Maven Central](https://central.sonatype.com/artifact/io.github.smfdrummer/medal-core) 上查看

</details>

</summary>

--------

## 使用

如需了解更多信息，请访问 [Wiki](https://github.com/SMFDrummer/Medal-core/wiki) 了解使用方法

--------

## 许可证

Medal-core 遵循 [AGPL 3.0](https://www.gnu.org/licenses/agpl-3.0.html) 许可发布

> 除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源
>
> 只有个人用途和开源的项目才能使用本库，否则需要收费
>
> 请遵守许可证规定，不要违反许可证规定，否则将追究法律责任

--------

## 致谢

感谢您使用 Medal-core，如果您有任何问题或建议，请在 [Issues](https://github.com/SMFDrummer/Medal-core/issues) 中提出

项目引用以下开源项目（排序不分先后）：

- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Kotlinx Serialization JsonPath](https://github.com/nomisRev/kotlinx-serialization-jsonpath)
- [Arrow-kt](https://github.com/arrow-kt/arrow)
- [Ktor](https://github.com/ktorio/ktor)
- [Bouncy Castle](https://www.bouncycastle.org)

--------

## 赞助

如果您喜欢 Medal-core 并希望支持项目的发展，您可以通过以下方式赞助：[爱发电](https://afdian.com/a/smfdrummer)

--------

#### 本项目使用 [Intellij IDEA](https://jetbrains.com/idea) 编写分发

#### 如果你喜欢本项目，请给一个 Star ⭐️，感谢您的支持