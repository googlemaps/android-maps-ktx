plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}


dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.gradle.maven.publish.plugin)
    implementation(libs.jacoco.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("publishingConventionPlugin") {
            id = "android.maps.ktx.PublishingConventionPlugin"
            implementationClass = "PublishingConventionPlugin"
        }
    }
}
