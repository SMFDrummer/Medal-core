import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm")
    alias(libs.plugins.mavenPublish)
    signing
    alias(libs.plugins.googleDevtoolsKsp)
}

dependencies {
    testImplementation(kotlin("test"))
    api(project(":shared"))
    ksp(libs.arrowOpticsKspPlugin)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

mavenPublishing {
    coordinates(project.group as String, "medal-core", project.version as String)

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

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}