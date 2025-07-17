plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}


dependencies {
    implementation(libs.kotlinGradlePlugin)
    implementation(libs.gradle)
    implementation(libs.dokkaGradlePlugin)
    implementation(libs.org.jacoco.core)
    implementation(libs.gradle.maven.publish.plugin)
}

gradlePlugin {
    plugins {
        register("publishingConventionPlugin") {
            id = "android.maps.ktx.PublishingConventionPlugin"
            implementationClass = "PublishingConventionPlugin"
        }
    }
}