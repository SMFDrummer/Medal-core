import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
    signing
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.kronosGradlePlugin)
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinGradlePlugin)

                api(libs.bundles.kotlinxEcosystem)
                api(libs.bundles.ktor)
                api(libs.bundles.arrow)
                api(libs.bundles.medalDependency)
                api(libs.bundles.kronos)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.logback)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.bundles.medalAndroidAddon)
            }
        }
    }
}

android {
    namespace = project.group as String
    compileSdk = 35
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = 26
    }
}

publishing {
    repositories {
        maven { url = uri(layout.buildDirectory.dir("repo")) }
    }
}

mavenPublishing {
    coordinates(project.group as String, "shared", project.version as String)

    pom {
        name = "Medal-core"
        description = "A core library for Medal project."
        url = "https://github.com/SMFDrummer/Medal-core"
        licenses {
            license {
                name = "GNU Affero General Public License v3.0"
                url = "https://www.gnu.org/licenses/agpl-3.0.html"
            }
        }
        developers {
            developer {
                id = "SMFDrummer"
                name = "SMFDrummer"
                url = "https://github.com/SMFDrummer"
            }
        }
        scm {
            connection = "scm:git:git://github.com/SMFDrummer/Medal-core.git"
            developerConnection = "scm:git:git://github.com/SMFDrummer/Medal-core.git"
            url = "https://github.com/SMFDrummer/Medal-core"
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}