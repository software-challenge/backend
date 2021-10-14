import sc.gradle.ScriptsTask

plugins {
    java
    application
}

sourceSets.main {
    java.srcDir("src")
}

application {
    mainClass.set("sc.TestClient")
}

dependencies {
    // TODO this is only here to access some default server Configuration, move that to SDK or smth
    implementation(project(":server"))
    runtimeOnly(project(":plugin"))
}

tasks {
    val createStartScripts by creating(ScriptsTask::class) {
        destinationDir = jar.get().destinationDirectory.get().asFile
        fileName = "start-tests"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback-tests.xml -jar test-client.jar"
    }
    
    jar {
        dependsOn(createStartScripts)
        doFirst {
            manifest {
                attributes(
                        "Class-Path" to
                                configurations.default.get()
                                        .map { "lib/" + it.name }
                                        .plus("server.jar")
                                        .joinToString(" "),
                        "Add-Opens" to arrayOf(
                                "javafx.controls/javafx.scene.control.skin",
                                "javafx.controls/javafx.scene.control",
                                "javafx.graphics/javafx.scene",
                                // For accessing InputMap used in RangeSliderBehavior
                                "javafx.controls/com.sun.javafx.scene.control.inputmap",
                                // Expose list internals for xstream conversion: https://github.com/x-stream/xstream/issues/253
                                "java.base/java.util").joinToString(" ")
                )
            }
            copy {
                from("src/logback-tests.xml")
                into(destinationDirectory)
            }
        }
    }
    
    run.configure {
        dependsOn(":player:shadowJar", ":server:makeRunnable")
        doFirst {
            setArgsString(System.getProperty("args") ?: run {
                val playerLocation = project(":player").tasks.getByName<Jar>("shadowJar").archiveFile.get()
                "--start-server --tests 3 --player1 $playerLocation --player2 $playerLocation"
            })
            @Suppress("UNNECESSARY_SAFE_CALL", "SimplifyBooleanWithConstants")
            if (args?.isEmpty() == false)
                println("Using command-line arguments: $args")
        }
    }
}
