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

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("android.maps.ktx.PublishingConventionPlugin")
}

android {
    lint {
        sarifOutput = file("$buildDir/reports/lint-results.sarif")
    }

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        testOptions.targetSdk = libs.versions.androidTargetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs += "-Xexplicit-api=strict"
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    namespace = "com.google.maps.android.ktx.utils"
}

dependencies {
    implementation(libs.kotlinStdlib)
    api(libs.androidMapsUtils)

    // Tests
    testImplementation(libs.androidxTest)
    testImplementation(libs.androidxJunit)
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoInline)
}
