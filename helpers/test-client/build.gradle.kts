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
    val createStartScripts by creating(ScriptsTask::class) {
        destinationDir = file("build/libs")
        fileName = "start-tests"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback-tests.xml -jar test-client.jar"
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
