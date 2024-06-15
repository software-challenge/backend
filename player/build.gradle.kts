import org.gradle.internal.os.OperatingSystem
import java.time.Duration

plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.1.0" // Update to v8 with Gradle update
}

val game: String by project
val year: String by project
val gameName: String by project
val testingDir: File by project
val version = rootProject.version.toString()

sourceSets.main {
    java.srcDir("src/main")
    resources.srcDir("src/resources")
}

application {
    mainClassName = "sc.player.util.Starter"
}

dependencies {
    implementation(project(":plugin$year"))
    implementation(kotlin("script-runtime"))
    implementation("ch.qos.logback", "logback-classic", "1.3.11") // Update to 1.4 with JDK upgrade
}

tasks {
    shadowJar {
        group = "distribution"
        archiveFileName.set("defaultplayer.jar")
    }
    
    val copyDocs by creating(Copy::class) {
        dependsOn(":sdk:doc", ":plugin$year:doc")
        into(buildDir.resolve("zip"))
        with(copySpec {
            from(project(":plugin$year").buildDir.resolve("doc"))
            into("doc/plugin-$gameName")
        }, copySpec {
            from(project(":sdk").buildDir.resolve("doc"))
            into("doc/sdk")
        })
    }
    
    val prepareZip by creating(Copy::class) {
        group = "distribution"
        into(buildDir.resolve("zip"))
        with(copySpec {
            from("configuration")
            filter {
                it.replace("VERSION", version).replace("GAME", game).replace("YEAR", year)
            }
        }, copySpec {
            from(rootDir.resolve("gradlew"), rootDir.resolve("gradlew.bat"))
            filter { it.replace(Regex("gradle([/\\\\])wrapper"), "lib$1gradle-wrapper") }
        }, copySpec {
            from(rootDir.resolve("gradle").resolve("wrapper"))
            into("lib/gradle-wrapper")
        }, copySpec {
            from("src")
            into("src")
            filter {
                it.replace("sc.api.plugins.IMove", "sc.plugin$year.Move")
                        .replace("IMove", "Move")
                        .replace("sc.api.plugins.TwoPlayerGameState", "sc.plugin$year.GameState")
                        .replace("TwoPlayerGameState<Move>", "GameState")
            }
        }, copySpec {
            from(configurations.default, arrayOf("sdk", "plugin$year")
                    .map { project(":$it").getTasksByName("sourcesJar", false) })
            into("lib")
        })
    }
    
    run.configure {
        args = System.getProperty("args", "").split(" ")
    }
    
    val deployDir: File by project
    val deployedPlayer: String by project
    val deployShadow by creating(Copy::class) {
        group = "distribution"
        from(shadowJar)
        into(deployDir)
        rename { deployedPlayer }
    }
    val deploy by creating(Zip::class) {
        group = "distribution"
        dependsOn(deployShadow)
        from(prepareZip, copyDocs)
        destinationDirectory.set(deployDir)
        archiveFileName.set("player-$gameName-src.zip")
    }
    
    val playerTest by creating(Exec::class) {
        group = "verification"
        dependsOn(prepareZip)
        val execDir = testingDir.resolve("player")
        doFirst {
            delete { delete(execDir) }
            copy {
                from(prepareZip)
                into(execDir)
            }
            // required by gradle to distinguish the test build
            execDir.resolve("settings.gradle").createNewFile()
        }
        
        timeout.set(Duration.ofMinutes(1))
        workingDir(execDir)
        executable = "./gradlew${if(OperatingSystem.current().isWindows) ".bat" else ""}"
        args("shadowJar", "--quiet", "--offline")
        
        doLast {
            exec {
                commandLine("java", "-jar", execDir.resolve("${game}_client.jar"), "--verify")
                standardOutput = org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM
            }
        }
    }
    
}
