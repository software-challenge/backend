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
    // TODO this dependency is only for accessing the Configuration, remove it
    implementation(project(":server"))
}

tasks {
    val createStartScripts by creating(CreateStartScripts::class) {
        outputDir = jar.get().destinationDirectory.asFile.get()
        applicationName = "start-tests"
        defaultJvmOpts = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=logback-tests.xml")
    }

    jar {
        dependsOn(createStartScripts)
        doFirst {
            manifest.attributes["Class-Path"] =
                configurations.default.get().map { "lib/" + it.name }
                        .plus("server.jar")
                        .joinToString(" ")
            copy {
                from("src/logback-tests.xml")
                into(destinationDirectory)
            }
        }
    }

    run.configure {
        args = System.getProperty("args", "").split(" ")
    }
}
