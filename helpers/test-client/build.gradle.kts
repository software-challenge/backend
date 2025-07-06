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

val year: String by project
dependencies {
    implementation(project(":sdk"))
    implementation(project(":server")) // Only to access defaults of sc.server.Configuration
    implementation("ch.qos.logback", "logback-classic", "1.3.15") // Update to 1.4 with JDK upgrade
    runtimeOnly(project(":plugin$year"))
}

tasks {
    val createStartScripts by creating(ScriptsTask::class) {
        destinationDir = jar.get().destinationDirectory.get().asFile
        fileName = "start-tests"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback-tests.xml -jar test-client.jar"
    }
    
    val copyLogbackConfig by creating(Copy::class) {
        from("src/logback-tests.xml")
        into(jar.get().destinationDirectory)
    }
    
    jar {
        dependsOn(createStartScripts, copyLogbackConfig)
        doFirst {
            manifest.attributes(
                    "Class-Path" to configurations.default.get()
                            .map { "lib/" + it.name }
                            .plus("server.jar")
                            .joinToString(" ")
            )
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
            if(args?.isEmpty() == false)
                println("Using command-line arguments: $args")
        }
    }
}
