plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka")
}

dependencies {
    dokka(project(":maps-utils-ktx"))
    dokka(project(":maps-ktx"))
}

dokka {
    moduleName.set("Maps Android KTX")
}