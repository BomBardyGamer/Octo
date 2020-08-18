/*
 * Octo, the simple yet responsive JDA command framework with advanced capabilities
 * Copyright (C) 2020 Callum Jay Seabrook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka") version "0.10.1"

    kotlin("jvm") version "1.4.0"
    `maven-publish`
}

group = "dev.bombardy"
version = "2.0.8"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

    implementation("net.dv8tion:JDA:4.2.0_192")
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
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("BomBardyGamer")
                        name.set("Callum Seabrook")
                        email.set("callum.seabrook@prevarinite.com")

                        roles.set(listOf("Developer", "Maintainer"))

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