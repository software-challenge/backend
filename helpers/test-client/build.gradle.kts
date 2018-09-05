plugins {
    java
    application
}

sourceSets {
    getByName("main").java.srcDir("src")
}

application {
    mainClassName = "sc.TestClient"
}

dependencies {
    compile(project(":plugins"))
    compile(project(":server"))
}

tasks {
    "jar"(Jar::class) {
        dependsOn("createScripts")
        doFirst {
            copy {
                from("src/logback-tests.xml")
                into("build/libs")
            }
        }
        manifest.attributes["Class-Path"] = configurations.default.joinToString(" ") { "lib/" + it.name } + " plugins/${project.property("game")}.jar software-challenge-server.jar"
    }

    create<ScriptsTask>("createScripts") {
        destinationDir = file("build/libs")
        fileName = "start-tests"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback-tests.xml -jar test-client.jar"
    }

    "run"(JavaExec::class) {
        args = System.getProperty("args", "").split(" ")
    }
}
