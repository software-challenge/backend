import org.gradle.api.GradleException
import org.gradle.internal.os.OperatingSystem
import java.time.Duration

plugins {
    application
    id("com.gradleup.shadow") version "9.3.1"
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
    mainClass.set("sc.player.util.Starter")
}

dependencies {
    api(project(":sdk"))
    implementation("ch.qos.logback", "logback-classic", "1.3.15")
    
    runtimeOnly(project(":plugin$year"))
    //runtimeOnly(kotlin("script-runtime"))
}

tasks {
    shadowJar {
        group = "distribution"
        archiveFileName.set("defaultplayer.jar")
    }
    
    val copyDocs by registering(Copy::class) {
        dependsOn(":sdk:doc", ":plugin$year:doc")
        into(layout.buildDirectory.dir("zip"))
        with(copySpec {
            from(project(":plugin$year").layout.buildDirectory.dir("doc"))
            into("doc/plugin-$gameName")
        }, copySpec {
            from(project(":sdk").layout.buildDirectory.dir("doc"))
            into("doc/sdk")
        })
    }
    
    val prepareZip by registering(Copy::class) {
        group = "distribution"
        into(layout.buildDirectory.dir("zip"))
        with(copySpec {
            from("configuration")
            filter {
                it.replace("VERSION", version).replace("GAME", game).replace("YEAR", year)
            }
        }, copySpec {
            from(rootDir.resolve("gradlew"), rootDir.resolve("gradlew.bat"))
        }, copySpec {
            from(rootDir.resolve("gradle").resolve("wrapper"))
            into("gradle/wrapper")
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
            from(configurations.default)
            from(arrayOf("sdk", "plugin$year").map { project(":$it").tasks.named("sourcesJar") })
            into("lib")
        })
    }
    
    run.configure {
        args = System.getProperty("args", "").split(" ")
        mustRunAfter(":server:run")
    }
    
    val bundleDir: File by project
    val bundledPlayer: String by project
    val bundleShadow by registering(Copy::class) {
        group = "distribution"
        from(shadowJar)
        into(bundleDir)
        rename { bundledPlayer }
    }
    val bundle by registering(Zip::class) {
        group = "distribution"
        dependsOn(bundleShadow)
        from(prepareZip, copyDocs)
        destinationDirectory.set(bundleDir)
        archiveFileName.set("player-$gameName-src.zip")
    }
    
    /** Build a player that times out. */
    val playerTest by registering(Exec::class) {
        group = "verification"
        dependsOn(prepareZip)
        val execDir = testingDir.resolve("player")
        doFirst {
            delete { delete(execDir) }
            copy {
                from(prepareZip)
                into(execDir)
            }
            val logic = execDir.resolve("src/main/sc/player/Logic.java")
            val lines = logic.readLines()
            logic.writeText(lines.joinToString(System.lineSeparator()) {
                it.replace(
                    "// Hier intelligente Strategie zur Auswahl des Zuges einfügen",
                    "try {Thread.sleep(3000);} catch(InterruptedException e) {throw new RuntimeException(e);}"
                )
            })
            // required by gradle to distinguish the test build
            execDir.resolve("settings.gradle").createNewFile()
        }
        
        timeout.set(Duration.ofMinutes(1))
        workingDir(execDir)
        executable = "./gradlew${if(OperatingSystem.current().isWindows) ".bat" else ""}"
        args("shadowJar", "--quiet", "--offline")
        
        doLast {
            val process = ProcessBuilder(
                "java",
                "-jar",
                execDir.resolve("${game}_client.jar").absolutePath,
                "--verify"
            )
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException("playerTest verification failed with exit code $exitCode")
            }
            println("Successfully built the shipped player package!")
        }
    }
    
    /** Run a player that hits the soft-timeout. */
    // TODO incorporate into a proper test
    val runTimeout by registering(Exec::class) {
        group = "verification"
        dependsOn(playerTest)
        doFirst {
            workingDir = playerTest.get().workingDir
        }
        commandLine("java", "-jar", file("${game}_client.jar").absolutePath)
    }
    
}
