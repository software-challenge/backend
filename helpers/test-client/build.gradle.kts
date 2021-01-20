import sc.gradle.ScriptsTask

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
    implementation(project(":plugin"))
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
            val libs = arrayListOf("plugins/${project.property("game")}.jar", "software-challenge-server.jar", "server.jar")
            libs.addAll(configurations.default.get().map  { "lib/" + it.name })
            manifest.attributes["Class-Path"] = libs.joinToString(" ")
            copy {
                from("src/logback-tests.xml")
                into("build/libs")
            }
        }
    }

    run.configure {
        args = System.getProperty("args", "").split(" ")
    }
}
