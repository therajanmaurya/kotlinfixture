/*
 * Copyright 2020 Appmattus Limited
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/therajanmaurya/fixture")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("USERNAME")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("TOKEN")
            }
        }
    }
}

include(
    "fixture",
    "fixture-javafaker",
    "fixture-kotest",
    "fixture-generex",
    "fixture-android-tests"
)
