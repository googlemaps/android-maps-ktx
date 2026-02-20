plugins {
    kotlin("jvm") apply false
    alias(libs.plugins.dokka)
}

dependencies {
    dokka(project(":maps-utils-ktx"))
    dokka(project(":maps-ktx"))
}

dokka {
    moduleName.set("Maps Android KTX")
}