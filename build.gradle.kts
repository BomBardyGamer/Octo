/*
 * Octo, the simple yet responsive JDA command framework with advanced capabilities
 * Copyright (C) 2020  Callum Jay Seabrook
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka") version "0.10.1"

    kotlin("jvm") version "1.3.72"
    `maven-publish`
}

group = "dev.bombardy"
version = "2.0.2"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")

    implementation("net.dv8tion:JDA:4.2.0_186")

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