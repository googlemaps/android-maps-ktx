dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        if (providers.gradleProperty("useMavenLocal").orNull == "true" ||
            providers.environmentVariable("USE_MAVEN_LOCAL").orNull == "true") {
            mavenLocal()
        }
        google()
        mavenCentral()
    }
}
pluginManagement {
    includeBuild("build-logic")
    repositories {
        if (providers.gradleProperty("useMavenLocal").orNull == "true" ||
            providers.environmentVariable("USE_MAVEN_LOCAL").orNull == "true") {
            mavenLocal()
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "android-maps-ktx"

include(":app")
include(":maps-ktx")
include(":maps-utils-ktx")
include(":docs")