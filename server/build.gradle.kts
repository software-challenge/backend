import sc.gradle.ScriptsTask

plugins {
    application
}

sourceSets {
    main.get().java.srcDir("src")
    test.get().java.srcDir("test")
}

application {
    mainClassName = "sc.server.Application"
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8",
            "-XX:+PrintGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-Xloggc:gc.log")
}

dependencies {
    implementation(project(":sdk"))
    runtimeOnly(project(":plugin"))
    
    testImplementation("junit", "junit", "4.13.1") // legacy java tests
}

val deployDir: File by project

tasks {
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
    
    val deploy by creating(Zip::class) {
        group = "distribution"
        dependsOn(project(":test-client").tasks.jar, ":player:shadowJar", makeRunnable)
        destinationDirectory.set(deployDir)
        archiveBaseName.set("software-challenge-server")
        from(runnableDir)
        if(project.property("enableTestClient") as Boolean)
            from(project(":test-client").buildDir.resolve("libs"))
        doFirst {
            from(project(":player").tasks["shadowJar"].outputs)
            exec {
                commandLine("git", "rev-parse", "HEAD")
                standardOutput = runnableDir.resolve("version").outputStream()
            }
        }
    }
    
    val startProduction by creating(JavaExec::class) {
        group = "application"
        dependsOn(makeRunnable)
        classpath = jar.get().outputs.files
        jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=../../logback-production.xml", "-Djava.security.egd=file:/dev/./urandom", "-XX:MaxGCPauseMillis=100", "-XX:GCPauseIntervalMillis=2050", "-XX:+UseConcMarkSweepGC", "-XX:+CMSParallelRemarkEnabled", "-XX:+UseCMSInitiatingOccupancyOnly", "-XX:CMSInitiatingOccupancyFraction=70", "-XX:+ScavengeBeforeFullGC", "-XX:+CMSScavengeBeforeRemark")
    }
    
    val dockerImage by creating(Exec::class) {
        group = "application"
        dependsOn(makeRunnable)
        workingDir = buildDir
		doFirst {
			val tag = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "--short", "--verify", "HEAD")).inputStream.reader().readText().trim()
			val relativeRunnable = runnableDir.relativeTo(workingDir)
			commandLine("docker", "build", "--no-cache", "-t", "swc_game-server:latest", "-t", "swc_game-server:$tag", "--build-arg", "game_server_dir=$relativeRunnable", ".")
            copy {
                from(projectDir.resolve("configuration"))
                include("?ocker*")
                into(workingDir)
            }
		}
    }
    
    jar {
        destinationDirectory.set(runnableDir)
        doFirst {
            manifest.attributes["Class-Path"] = configurations.default.get().joinToString(" ") { "lib/" + it.name }
        }
    }
    
    run.configure {
        dependsOn(copyConfig)
        workingDir = runnableDir
        jvmArgs = listOf("-Dlogback.configurationFile=../../configuration/logback.xml")
        args = System.getProperty("args", "").split(" ")
    }
    
}
