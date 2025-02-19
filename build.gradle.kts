allprojects {
    group = project.group as String
    version = project.version as String

    repositories {
        mavenLocal()
        google()
        mavenCentral()
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
    kotlin("jvm") version "2.1.0" apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinPluginSerialization) apply false
    alias(libs.plugins.googleDevtoolsKsp) apply false
    alias(libs.plugins.kronosGradlePlugin) apply false
}
