import java.util.Scanner

plugins {
    application
}

java.sourceSets {
    "main" { java.srcDir("src") }
    "test" { java.srcDir("test") }
}

application {
    mainClassName = "sc.server.Application"
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8",
            "-XX:+PrintGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-Xloggc:gc.log")
}

dependencies {
    compile(project(":sdk"))
    compile(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}

tasks {
    val runnable = file("build/runnable")

    "zip"(Zip::class) {
        dependsOn(":test-client:jar", ":players:jar", "makeRunnable")
        destinationDir = file("../build/deploy")
        baseName = "software-challenge-server"
        from(project(":test-client").buildDir.resolve("libs"), project(":players").buildDir.resolve("libs"), runnable)
        doFirst {
            runnable.resolve("version").writeText(Scanner(Runtime.getRuntime().exec("git rev-parse HEAD").inputStream).next())
        }
    }

    "startProduction"(JavaExec::class) {
        dependsOn("makeRunnable")
        classpath = files(configurations.compile, runnable.resolve("software-challenge-server.jar"))
        main = "sc.server.Application"
        workingDir = runnable
        jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=../../logback-production.xml", "-Djava.security.egd=file:/dev/./urandom", "-XX:MaxGCPauseMillis=100", "-XX:GCPauseIntervalMillis=2050", "-XX:+UseConcMarkSweepGC", "-XX:+CMSParallelRemarkEnabled", "-XX:+UseCMSInitiatingOccupancyOnly", "-XX:CMSInitiatingOccupancyFraction=70", "-XX:+ScavengeBeforeFullGC", "-XX:+CMSScavengeBeforeRemark")
    }

    "dockerImage"(Exec::class) {
        dependsOn("makeRunnable")
        val tag = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "--short", "--verify", "HEAD")).inputStream.reader().readText()
        commandLine("docker", "build", "--no-cache", "-t", "swc_game-server:latest", "-t", "swc_game-server:$tag", "--build-arg", "game_server_dir=$runnable", ".")
    }

    "makeRunnable"(Copy::class) {
        dependsOn("jar", "copyPlugin", "createScripts")
        from(configurations.compile)
        into(runnable.resolve("lib"))
    }

    "createScripts"(ScriptsTask::class) {
        destinationDir = runnable
        fileName = "start"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback.xml -jar software-challenge-server.jar"
    }

    "copyPlugin"(Copy::class) {
        dependsOn(":plugins:jar")
        from(project(":plugins").buildDir.resolve("libs"))
        into(runnable.resolve("plugins"))
        doLast {
            copy {
                from("configuration/logback-release.xml", "configuration/server.properties.example")
                into(runnable)
                rename("logback-release.xml", "logback.xml")
                rename("server.properties.example", "server.properties")
            }
        }
    }

    "jar"(Jar::class) {
        destinationDir = runnable
        baseName = "software-challenge-server"
        manifest {
            attributes["Main-Class"] = application.mainClassName
            attributes["Class-Path"] = configurations.compile.joinToString(" ") { "lib/" + it.name }
        }
    }

    "run"(JavaExec::class) {
        dependsOn("copyPlugin")
        workingDir = runnable
        jvmArgs = listOf("-Dlogback.configurationFile=../../configuration/logback.xml")
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