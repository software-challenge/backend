plugins {
    java
    application
}

java.sourceSets {
    "main" { java.srcDir("src") }
}

application {
    mainClassName = "sc.TestClient"
}

dependencies {
    compile(project(":plugins"))
    compile(project(":server"))
}

tasks {
    "jar" {
        dependsOn("createScripts")
        doFirst {
            copy {
                from("src/logback-tests.xml")
                into("build/libs")
            }
        }
    }

    "createScripts"(ScriptsTask::class) {
        destinationDir = file("build/libs")
        fileName = "start-tests"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback-tests.xml -jar test-client.jar"
    }

    "run"(JavaExec::class) {
        args = System.getProperty("args", "").split(" ")
    }
}

fun createScript(dir: File, name: String, content: String) {
    dir.resolve("$name.bat").run {
        writeText(content)
        setExecutable(true)
    }
    dir.resolve("$name.sh").run {
        writeText("#!/bin/sh\n$content")
        setExecutable(true)
    }
}