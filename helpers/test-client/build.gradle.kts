plugins {
    java
    application
}

sourceSets.main {
    java.srcDir("src")
}

application {
    mainClassName = "sc.TestClient"
}

dependencies {
    compile(project(":plugin"))
    compile(project(":server"))
}

tasks {
    val createScripts by creating(ScriptsTask::class) {
        destinationDir = file("build/libs")
        fileName = "start-tests"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback-tests.xml -jar test-client.jar"
    }

    jar {
        dependsOn(createScripts)
        doFirst {
            copy {
                from("src/logback-tests.xml")
                into("build/libs")
            }
        }
        val libs = arrayListOf("plugins/${project.property("game")}.jar", "software-challenge-server.jar", "server.jar")
        libs.addAll(configurations.default.get().map  { "lib/" + it.name })
        manifest.attributes["Class-Path"] = libs.joinToString(" ")
    }

    run.configure {
        args = System.getProperty("args", "").split(" ")
    }
}
