import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka") version "0.10.1"

    kotlin("jvm") version "1.3.72"
    `maven-publish`
}

group = "dev.bombardy"
version = "2.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    implementation("net.dv8tion:JDA:4.2.0_173")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<DokkaTask> {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

task<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from("$buildDir/javadoc")
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = "https://repo.prevarinite.com/repository/maven-releases/"
            val snapshotsRepoUrl = "https://repo.prevarinite.com/repository/maven-snapshots/"

            maven {
                credentials {
                    username = System.getProperty("maven-user")
                    password = System.getProperty("maven-pass")
                }

                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            }
        }
    }

    publications {
        create<MavenPublication>("octo") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("octo")
                description.set("A simple yet responsive framework.")
                url.set("https://octo.bombardy.dev/")

                licenses {
                    license {
                        name.set("The GNU General Public License v3")
                        url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    }
                }

                developers {
                    developer {
                        id.set("BomBardyGamer")
                        name.set("Callum Seabrook")
                        email.set("callum.seabrook@prevarinite.com")

                        organization.set("Prevarinite")
                        organizationUrl.set("https://www.prevarinite.com/")
                    }
                }
            }

            artifactId = "octo"
            version = project.version.toString()
        }
    }
}