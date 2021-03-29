import org.gradle.internal.os.OperatingSystem

plugins {
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

val game: String by project
val year: String by project
val gameName: String by project
val deployDir: File by project
val testingDir: File by project
val version = rootProject.version.toString()

sourceSets.main {
    java.srcDir("src/main")
    resources.srcDir("src/resources")
}

application {
    mainClassName = "sc.player2021.Starter"
}

dependencies {
    implementation(project(":plugin"))
    implementation(kotlin("script-runtime"))
}

tasks {
    shadowJar {
        group = "distribution"
        archiveFileName.set("defaultplayer.jar")
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
        }, copySpec {
            from(configurations.default, arrayOf("sdk", "plugin").map { project(":$it").getTasksByName("sourcesJar", false).single().outputs.files })
            into("lib")
        })
        if(!project.hasProperty("nodoc")) {
            dependsOn(":sdk:doc", ":plugin:doc")
            with(copySpec {
                from(project(":plugin").buildDir.resolve("doc"))
                into("doc/plugin-$gameName")
            }, copySpec {
                from(project(":sdk").buildDir.resolve("doc"))
                into("doc/sdk")
            })
        }
    }
    
    val deploy by creating(Zip::class) {
        group = "distribution"
        dependsOn(shadowJar, prepareZip)
        destinationDirectory.set(deployDir)
        archiveFileName.set("simpleclient-$gameName-src.zip")
        from(prepareZip.destinationDir)
        doFirst {
            shadowJar.get().outputs.files.singleFile.copyTo(
                    deployDir.resolve(project.property("deployedPlayer") as String), true)
        }
    }
    
    run.configure {
        args = System.getProperty("args", "").split(" ")
    }
    
    val playerTest by creating(Copy::class) {
        group = "verification"
        dependsOn(prepareZip)
        
        val execDir = testingDir.resolve("player")
        doFirst {
            execDir.deleteRecursively()
            execDir.mkdirs()
        }
        from(prepareZip.destinationDir)
        into(execDir)
        
        doLast {
            // required by gradle to distinguish the test build from
            execDir.resolve("settings.gradle").createNewFile()
            val command = arrayListOf(if(OperatingSystem.current() == OperatingSystem.WINDOWS) "./gradlew.bat" else "./gradlew",
                    "shadowJar", "--quiet", "--offline")
            val process = ProcessBuilder(command).directory(execDir)
                    .redirectOutput(execDir.resolve("player-shadowJar-build.log"))
                    .redirectError(execDir.resolve("player-shadowJar-err.log"))
                    .start()
            val timeout = 5L
            if(process.waitFor(timeout, TimeUnit.MINUTES)) {
                val result = process.exitValue()
                if(result != 0 || !execDir.resolve("${game}_client.jar").exists())
                    throw Exception("Player was not generated by shipped gradlew script!")
            } else {
                throw Exception("Gradlew shadowJar for player did not finish within $timeout minutes!")
            }
            println("Successfully generated client jar from shipped source")
        }
    }
    
}
