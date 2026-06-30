// buildSrc/src/main/kotlin/PublishingConventionPlugin.kt
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.run {
            applyPlugins()
            configureJacoco()
            configureVanniktechPublishing()
        }
    }

    private fun Project.applyPlugins() {
        apply(plugin = "com.android.library")
        apply(plugin = "com.mxalbert.gradle.jacoco-android")
        apply(plugin = "org.jetbrains.dokka")
        apply(plugin = "com.vanniktech.maven.publish")
    }

    private fun Project.configureJacoco() {
        configure<JacocoPluginExtension> {
            toolVersion = "0.8.12"
        }

        tasks.withType<Test>().configureEach {
            // Support Mockito ByteBuddy dynamic agent loading, add-opens reflection, and prevent CDS bytecode sharing issues on JDK 26+
            jvmArgs(
                "-XX:+EnableDynamicAgentLoading",
                "-Xshare:off",
                "--add-opens", "java.base/java.lang=ALL-UNNAMED",
                "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
                "--add-opens", "java.base/java.io=ALL-UNNAMED",
                "--add-opens", "java.base/java.util=ALL-UNNAMED",
                "-Dnet.bytebuddy.experimental=true"
            )
            extensions.configure(JacocoTaskExtension::class.java) {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*", "sun.*", "java.*", "jdk.*")
            }
        }
    }

    private fun Project.configureVanniktechPublishing() {
        extensions.configure<MavenPublishBaseExtension> {
            configure(
                AndroidSingleVariantLibrary(
                    variant = "release",
                    sourcesJar = true,
                    publishJavadocJar = true
                )
            )

            publishToMavenCentral()
            signAllPublications()

            pom {
                name.set(project.name)
                description.set("Kotlin extensions (KTX) for Google Maps SDK")
                url.set("https://github.com/googlemaps/android-maps-ktx")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:googlemaps/android-maps-ktx.git")
                    developerConnection.set("scm:git@github.com:googlemaps/android-maps-ktx.git")
                    url.set("https://github.com/googlemaps/android-maps-ktx")
                }
                organization {
                    name.set("Google Inc")
                    url.set("http://developers.google.com/maps")
                }
                developers {
                    developer {
                        id.set("google")
                        name.set("Google Inc.")
                    }
                }
            }
        }
    }
}
