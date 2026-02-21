/*
 * Shared JVM conventions for all subprojects.
 *
 * Kotlin DSL alternative to gradle/jvm-conventions.gradle.
 * Not wired into the build by default.
 */

import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.tasks.testing.Test
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val javaToolchainVersion = rootProject.extra["javaToolchainVersion"] as Int
val javaTargetVersion = rootProject.extra["javaTargetVersion"] as JavaVersion

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")

    java {
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

    tasks.withType(KotlinJvmCompile::class.java).configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(javaTargetVersion.toString()))

            val current = freeCompilerArgs.get().toMutableList()
            current.removeAll { it == "-Xjvm-default=all" }
            if (!current.contains("-jvm-default=no-compatibility")) {
                current.add("-jvm-default=no-compatibility")
            }
            if (!current.contains("-Xconsistent-data-class-copy-visibility")) {
                current.add("-Xconsistent-data-class-copy-visibility")
            }
            freeCompilerArgs.set(current)
        }
    }
}
