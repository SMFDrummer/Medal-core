import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20-2.0.1"
    id("com.vanniktech.maven.publish") version "0.31.0-rc2"
}

group = project.group as String
version = project.version as String

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation(platform("io.ktor:ktor-bom:3.1.3"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("org.slf4j:slf4j-nop:2.1.0-alpha1")

    implementation(platform("io.arrow-kt:arrow-stack:2.1.0"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-core-serialization")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-resilience")
    implementation("io.arrow-kt:arrow-optics")
    implementation("io.arrow-kt:arrow-optics-ksp-plugin")
    implementation("io.arrow-kt:arrow-atomic")
    implementation("io.arrow-kt:arrow-collectors")
    implementation("io.arrow-kt:arrow-eval")
    implementation("io.arrow-kt:arrow-cache4k")
    implementation("io.github.nomisrev:kotlinx-serialization-jsonpath:2.0.0-alpha.1")

    implementation("org.bouncycastle:bcprov-jdk18on:1.80")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

publishing {
    repositories {
        maven { url = uri(layout.buildDirectory.dir("repo")) }
    }
}

mavenPublishing {
    coordinates(group as String, "medal-core", version as String)

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