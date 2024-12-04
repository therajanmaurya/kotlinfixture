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

import com.vanniktech.maven.publish.GradlePublishPlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.android.lint")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.kotlin
    id("com.vanniktech.maven.publish")
}

apply(from = "$rootDir/gradle/scripts/jacoco.gradle.kts")

dependencies {
    api(kotlin("stdlib-jdk8"))
    api("io.github.classgraph:classgraph:${Versions.classgraph}")
    api(kotlin("reflect"))

    compileOnly("joda-time:joda-time:${Versions.jodaTime}")
    testImplementation("joda-time:joda-time:${Versions.jodaTime}")

    compileOnly("org.threeten:threetenbp:${Versions.threeTen}")
    testImplementation("org.threeten:threetenbp:${Versions.threeTen}")

    compileOnly("org.ktorm:ktorm-core:${Versions.kTorm}")
    testImplementation("org.ktorm:ktorm-core:${Versions.kTorm}")

    compileOnly(files("/Users/rajan.maurya/Library/Android/sdk/platforms/android-34/android.jar"))

    testImplementation("junit:junit:${Versions.junit4}")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockitoKotlin}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinX.serialization}")

    // Used for ComparisonTest
    @Suppress("GradleDependency")
    testImplementation("com.github.marcellogalhardo:kotlin-fixture:${Versions.marcellogalhardo}")
    testImplementation("com.flextrade.jfixture:kfixture:${Versions.flextrade}")
    testImplementation("org.jeasy:easy-random-core:${Versions.easyrandom}")
}

lint {
    abortOnError = true
    warningsAsErrors = true
    htmlOutput = file("$buildDir/reports/lint-results.html")
    xmlOutput = file("$buildDir/reports/lint-results.xml")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}

tasks.named("check") {
    finalizedBy(rootProject.tasks.named("detekt"))
    finalizedBy(rootProject.tasks.named("markdownlint"))
}

group = "io.github.therajanmaurya"
version = "1.0.0"

mavenPublishing {

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    signAllPublications()

    coordinates("io.github.therajanmaurya", "fixture", "1.0.0")

    pom {
        name = "Kotlin Fixture"
        description = "A library providing kotlinfixture"
        inceptionYear = "2024"
        url = "https://github.com/therajanmaurya/kotlinfixture"
        licenses {
            license {
                name = "Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "therajanmaurya"
                name = "Rajan Maurya"
                url = "https://github.com/therajanmaurya"
            }
        }
        scm {
            url = "https://github.com/therajanmaurya/kotlinfixture"
            connection = "scm:git:git://github.com/therajanmaurya/kotlinfixture.git"
            developerConnection = "scm:git:ssh://git@github.com:therajanmaurya/kotlinfixture.git"
        }
    }
}
