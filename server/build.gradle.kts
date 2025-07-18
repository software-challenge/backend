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
dependencies {
    api(project(":sdk"))
    implementation("ch.qos.logback", "logback-classic", "1.5.18") // Update to 1.4 with JDK upgrade
    
    runtimeOnly(project(":plugin$year"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine") // legacy java tests
}

val bundleDir: File by project
val isBeta: Boolean by project

tasks {
    test {
        systemProperty("junit.jupiter.execution.timeout.default", "10 s") // legacy junit tests
    }
    
    val runnableDir = buildDir.resolve("runnable")
    
    val createStartScripts by creating(ScriptsTask::class) {
        destinationDir = runnableDir
        fileName = "start"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback.xml -jar server.jar"
    }
    
    val copyConfig by creating(Copy::class) {
        group = "distribution"
        from("configuration/logback-release.xml", "configuration/server.properties.example")
        into(runnableDir)
        rename("logback-release.xml", "logback.xml")
        rename("server.properties.example", "server.properties")
    }
    
    val makeRunnable by creating(Copy::class) {
        group = "distribution"
        dependsOn(jar, copyConfig, createStartScripts)
        from(configurations.default)
        into(runnableDir.resolve("lib"))
    }
    
    val bundle by creating(Zip::class) {
        group = "distribution"
        dependsOn(":test-client:jar", ":player:shadowJar", makeRunnable)
        destinationDirectory.set(bundleDir)
        archiveBaseName.set("software-challenge-server")
        from(runnableDir)
        doFirst {
            if(project.property("enableTestClient") !in arrayOf(null, false))
                from(project(":test-client").getTasksByName("copyLogbackConfig", false))
            from(project(":player").getTasksByName("shadowJar", false))
            exec {
                commandLine("git", "describe", "--long", "--tags")
                standardOutput = runnableDir.resolve("version").outputStream()
            }
        }
    }
    
    val startProduction by creating(JavaExec::class) {
        group = "application"
        dependsOn(makeRunnable)
        classpath = jar.get().outputs.files
        jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=../../logback-production.xml", "-Djava.security.egd=file:/dev/./urandom",
            "-XX:MaxGCPauseMillis=100", "-XX:GCPauseIntervalMillis=2050", "-XX:+ScavengeBeforeFullGC")
    }
    
    val dockerImage by creating(Exec::class) {
        group = "application"
        dependsOn(makeRunnable)
        workingDir = buildDir
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
                    "Class-Path" to configurations.default.get().joinToString(" ") { "lib/" + it.name }
            )
        }
    }
    
    run.configure {
        dependsOn(copyConfig)
        workingDir = runnableDir
        jvmArgs = listOf("-Dlogback.configurationFile=../../configuration/logback.xml")
        args = System.getProperty("args", "").split(" ")
    }
}
