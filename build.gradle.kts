/**
 * Copyright 2024 Google Inc.
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

buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.dokkaGradlePlugin)
        classpath(libs.kotlinGradlePlugin)
        classpath(libs.jacocoAndroid)
        classpath(libs.secretsGradlePlugin)
    }
}

val projectArtifactId: (Project) -> String? = { project ->
    if (project.name == "maps-utils-ktx" || project.name == "maps-ktx") {
        project.name
    } else {
        null
    }
}

/**
 * Shared configs across subprojects
 */

allprojects {
    group = "com.google.maps.android"
    version = "5.2.0"
    val projectArtifactId by extra { project.name }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
