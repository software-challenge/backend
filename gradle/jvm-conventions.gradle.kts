/*
 * Shared JVM conventions for all subprojects.
 */

import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.tasks.testing.Test

val javaToolchainVersion = rootProject.extra["javaToolchainVersion"] as Int
val javaTargetVersion = rootProject.extra["javaTargetVersion"] as JavaVersion
val kotlinCompileTaskNames = setOf("compileKotlin", "compileTestKotlin")

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")

    extensions.configure(JavaPluginExtension::class.java) {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaToolchainVersion))
        }
    }

    if (name != "sdk" && name != "test-config") {
        dependencies {
            add("testImplementation", project(":test-config"))
        }
    }

    tasks.withType(Test::class.java).configureEach {
        useJUnitPlatform()
    }

    tasks.withType(JavaCompile::class.java).configureEach {
        options.release.set(javaTargetVersion.majorVersion.toInt())
        if (!options.compilerArgs.contains("-Xlint:-options")) {
            options.compilerArgs.add("-Xlint:-options")
        }
    }

    tasks
        .matching { it.name in kotlinCompileTaskNames }
        .configureEach {
            val kotlinOptions = if (hasProperty("kotlinOptions")) property("kotlinOptions") else null
            if (kotlinOptions != null) {
                kotlinOptions.javaClass.getMethod("setJvmTarget", String::class.java)
                    .invoke(kotlinOptions, javaTargetVersion.toString())
            }
        }
}
