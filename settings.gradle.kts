dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
pluginManagement {
    includeBuild("build-logic")
    repositories {
        if (System.getenv("USE_MAVEN_LOCAL") == "true") {
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