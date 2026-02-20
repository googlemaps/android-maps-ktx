/*
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
 *
 */

plugins {
    id("android.maps.ktx.PublishingConventionPlugin")
    alias(libs.plugins.dokka)
}

android {
    lint {
        sarifOutput = file("${layout.buildDirectory.get()}/reports/lint-results.sarif")
    }

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets["main"].java.srcDir("build/generated/source/artifactId")

    kotlin {
        compilerOptions {
            freeCompilerArgs.add("-Xexplicit-api=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }

    buildFeatures {
        viewBinding = true
    }

    namespace = "com.google.maps.android.ktx"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.startup)
    api(libs.play.services.maps)

    // Tests
    testImplementation(libs.androidx.test)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}

// START: Attribution ID Generation Logic
val attributionId = "gmp_git_androidmapsktx_v$version"

val generateArtifactIdFile = tasks.register("generateArtifactIdFile") {
    description = "Generates an AttributionId object from the project version."
    group = "build"

    val outputDir = layout.buildDirectory.dir("generated/source/artifactId")
    val packageName = "com.google.maps.android.ktx.utils.meta"
    val packagePath = packageName.replace('.', '/')
    val outputFile = outputDir.get().file("$packagePath/AttributionId.java").asFile

    outputs.file(outputFile)

    doLast {
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package $packageName;

            /**
             * Automatically generated object containing the library's attribution ID.
             * This is used to track library usage for analytics.
             */
            public final class AttributionId {
                public static final String VALUE = "$attributionId";
                private AttributionId() {}
            }
            """.trimIndent()
        )
    }
}

tasks.named("preBuild") {
    dependsOn(generateArtifactIdFile)
}
// END: Attribution ID Generation Logic
