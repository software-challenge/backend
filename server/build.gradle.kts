plugins {
    application
    // TODO https://github.com/CAU-Kiel-Tech-Inf/backend/issues/265
    distribution
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
    implementation("ch.qos.logback", "logback-classic", "1.3.15") // Update to 1.4 with JDK upgrade
    
    runtimeOnly(project(":plugin$year"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine") // legacy java tests
}

val bundleDir: File by project
val isBeta: Boolean by project

distributions {
    main {
        contents {
            into('') {
                from("$buildDir/install/${project.name}") {
                    include '*.bat', '*'
                    exclude 'bin/**'
                }
            }
        }
    }
}

tasks {
    test {
        systemProperty("junit.jupiter.execution.timeout.default", "10 s") // legacy junit tests
    }
    
    val runnableDir = buildDir.resolve("runnable")
    
    startScripts {
        println(executableDir.toString())
        outputDir = runnableDir
        applicationName = "start-server"
        defaultJvmOpts = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=logback.xml")
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
        dependsOn(jar, copyConfig, startScripts)
        from(configurations.default)
        into(runnableDir.resolve("lib"))
    }
    
    distributions {
        main {
            distributionBaseName.set("software-challenge-server")
            contents {
                from(runnableDir)
                doFirst {
                    if(project.property("enableTestClient") !in arrayOf(null, false))
                        from(project(":test-client").getTasksByName("copyLogbackConfig", false))
                    from(project(":player").getTasksByName("shadowJar", false))
                    
                    val versionFile = runnableDir.resolve("version")
                    try {
                        exec {
                            commandLine("git", "describe", "--long", "--tags")
                            standardOutput = versionFile.outputStream()
                        }
                    } catch(e: Exception) {
                        println("Issue with git describe for version detection, falling back to rev-parse: $e")
                        println(versionFile.readText())
                        exec {
                            commandLine("git", "rev-parse", "HEAD")
                            standardOutput = versionFile.outputStream()
                        }
                    }
                }
            }
        }
    }
    
    distZip {
        dependsOn(":test-client:jar", ":player:shadowJar", makeRunnable)
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
        destinationDirectory.set(runnableDir.resolve("lib"))
        doFirst {
            manifest.attributes(
                    "Class-Path" to
                    configurations.default.get().joinToString(" ") { "lib/" + it.name })
        }
    }
    
    run.configure {
        dependsOn(copyConfig)
        workingDir = runnableDir
        jvmArgs = listOf("-Dlogback.configurationFile=../../configuration/logback.xml")
        args = System.getProperty("args", "").split(" ")
    }
}
