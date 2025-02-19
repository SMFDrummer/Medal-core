dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven {
            credentials {
                username = "6661af0c55d469d21fb755c5"
                password = "=3o4EjHlYVT_"
            }
            url = uri("https://packages.aliyun.com/6661af21262ae18c31667f7d/maven/kronos-orm")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Medal-core"
include(":shared", ":medal-core", ":medal-core-android")
