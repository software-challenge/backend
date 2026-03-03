import org.gradle.api.GradleException
import sc.gradle.ScriptsTask

plugins {
    application
}

application {
    mainClass.set("sc.server.Application")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8",
            "-XX:+PrintGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-Xloggc:gc.log" // GC
    )
}

val year: String by project
val enableTestClient: Boolean by project
dependencies {
    api(project(":sdk"))
    implementation("ch.qos.logback:logback-classic:1.5.32") // Update to 1.4 with JDK upgrade
    
    runtimeOnly(project(":plugin$year"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine") // legacy java tests
}

val bundleDir: File by project
val isBeta: Boolean by project

tasks {
    test {
        systemProperty("junit.jupiter.execution.timeout.default", "10 s") // legacy junit tests
    }
    
    val runnableDir = layout.buildDirectory.dir("runnable").get().asFile
    
    val createStartScripts by registering(ScriptsTask::class) {
        destinationDir = runnableDir
        fileName = "start"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback.xml -jar server.jar"
    }

    val copyConfig by registering(Copy::class) {
        group = "distribution"
        from("configuration/logback-release.xml", "configuration/server.properties.example")
        into(runnableDir)
        rename("logback-release.xml", "logback.xml")
        rename("server.properties.example", "server.properties")
    }
    
    val makeRunnable by registering(Copy::class) {
        group = "distribution"
        dependsOn(jar, copyConfig, createStartScripts)
        from(configurations.runtimeClasspath)
        into(runnableDir.resolve("lib"))
    }
    
    val bundle by registering(Zip::class) {
        group = "distribution"
        dependsOn(":player:shadowJar", makeRunnable)
        destinationDirectory.set(bundleDir)
        archiveBaseName.set("software-challenge-server")
        from(runnableDir)
        if (enableTestClient) {
            dependsOn(":test-client:jar", ":test-client:copyLogbackConfig")
            from({ project(":test-client").tasks.getByName("copyLogbackConfig").outputs.files })
        }
        from({ project(":player").tasks.getByName("shadowJar").outputs.files })
        doFirst {
            val versionFile = runnableDir.resolve("version")
            try {
                val describe = ProcessBuilder("git", "describe", "--long", "--tags")
                    .redirectOutput(versionFile)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                if (describe.waitFor() != 0) {
                    throw GradleException("git describe failed")
                }
            } catch(e: Exception) {
                println("Issue with git describe for version detection, falling back to rev-parse: $e")
                println(versionFile.readText())
                val revParse = ProcessBuilder("git", "rev-parse", "HEAD")
                    .redirectOutput(versionFile)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                if (revParse.waitFor() != 0) {
                    throw GradleException("git rev-parse failed")
                }
            }
        }
    }
    
    val startProduction by registering(JavaExec::class) {
        group = "application"
        dependsOn(makeRunnable)
        classpath = jar.get().outputs.files
        jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=../../logback-production.xml", "-Djava.security.egd=file:/dev/./urandom",
            "-XX:MaxGCPauseMillis=100", "-XX:GCPauseIntervalMillis=2050", "-XX:+ScavengeBeforeFullGC")
    }
    
    val dockerImage by registering(Exec::class) {
        group = "application"
        dependsOn(makeRunnable)
        workingDir = layout.buildDirectory.get().asFile
        doFirst {
            val tag = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "--short", "--verify", "HEAD"))
                    .inputStream.reader().readText().trim()
            val relativeRunnable = runnableDir.relativeTo(workingDir)
            commandLine("docker", "build", "--no-cache", "-t", "swc_game-server:latest", "-t", "swc_game-server:$tag", "--build-arg", "game_server_dir=$relativeRunnable", ".")
            copy {
                from(projectDir.resolve("configuration"))
                include("?ocker*")
                include("server.properties.production")
                val suffix = if(isBeta) "trace" else "production"
                include("logback-$suffix.xml")
                rename("logback-$suffix.xml", "logback.xml")
                into(workingDir)
            }
        }
    }
    
    jar {
        destinationDirectory.set(runnableDir)
        doFirst {
            manifest.attributes(
                    "Class-Path" to configurations.runtimeClasspath.get().joinToString(" ") { "lib/" + it.name }
            )
        }
    }
    
    run.configure {
        dependsOn(copyConfig)
        workingDir = runnableDir
        jvmArgs = listOf("-Dlogback.configurationFile=../../configuration/logback.xml")
        args = System.getProperty("args", "").split(" ")
    }

    // Keep application plugin for run, but disable generated distribution tasks.
    listOf("distZip", "distTar", "installDist", "startScripts").forEach { taskName ->
        named(taskName) {
            enabled = false
        }
    }
}
