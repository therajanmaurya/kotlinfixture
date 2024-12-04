/*
 * Copyright 2021-2023 Appmattus Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI

plugins {
    kotlin("jvm") version Versions.kotlin apply false
    id("io.gitlab.arturbosch.detekt") version Versions.detektGradlePlugin
    id("com.appmattus.markdown") version Versions.markdownlintGradlePlugin
    id("com.vanniktech.maven.publish") version Versions.gradleMavenPublishPlugin apply false
    id("org.jetbrains.dokka") version Versions.dokkaPlugin
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePlugin}")
    }
}

apply(from = "$rootDir/gradle/scripts/dependencyUpdates.gradle.kts")
apply(from = "$rootDir/owaspDependencyCheck.gradle.kts")

allprojects {

    version = (System.getenv("GITHUB_REF") ?: System.getProperty("GITHUB_REF"))
        ?.replaceFirst("refs/tags/", "") ?: "unspecified"

    plugins.withType<DokkaPlugin> {
        tasks.withType<DokkaTask>().configureEach {
            dokkaSourceSets {
                configureEach {
                    skipDeprecated.set(true)

                    if (name.startsWith("ios")) {
                        displayName.set("ios")
                    }

                    sourceLink {
                        localDirectory.set(rootDir)
                        remoteUrl.set(URI("https://github.com/appmattus/kotlinfixture/blob/main").toURL())
                        remoteLineSuffix.set("#L")
                    }
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detektGradlePlugin}")
}

detekt {
    source = files(fileTree(projectDir).matching {
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
    }.files)

    // input = files("$projectDir")

    buildUponDefaultConfig = true

    autoCorrect = true

    config = files("detekt-config.yml")
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            
            packageGroup.set("io.github.therajanmaurya")
            
            username.set(providers.gradleProperty("sonatypeUsername").get())
            password.set(providers.gradleProperty("sonatypePassword").get())
        }
    }
    
    transitionCheckOptions {
        maxRetries.set(60)
        delayBetween.set(java.time.Duration.ofSeconds(5))
    }
}
