import org.gradle.kotlin.dsl.support.unzipTo
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import sc.gradle.ScriptsTask
import java.nio.file.Files
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

plugins {
    maven
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.dokka") version "0.10.1"
    id("scripts-task")
    
    id("com.github.ben-manes.versions") version "0.42.0" // only upgrade with Gradle 7: https://github.com/ben-manes/gradle-versions-plugin/issues/778
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
}

val gameName by extra { property("socha.gameName") as String }
val versions = arrayOf("year", "minor", "patch").map { property("socha.version.$it").toString().toInt() }
val versionObject = KotlinVersion(versions[0], versions[1], versions[2])
version = versionObject.toString() + property("socha.version.suffix").toString().takeUnless { it.isBlank() }?.let { "-$it" }.orEmpty()
val year by extra { "20${versionObject.major}" }
val game by extra { "${gameName}_$year" }

val bundleDir by extra { buildDir.resolve("bundle") }
val bundledPlayer by extra { "randomplayer-$gameName-$version.jar" }
val testingDir by extra { buildDir.resolve("tests") }
val documentedProjects = listOf("sdk", "plugin$year")

val isBeta by extra { versionObject.minor == 0 }
val enableTestClient by extra { arrayOf("check", "testTestClient").any { gradle.startParameter.taskNames.contains(it) } || !isBeta }
val enableIntegrationTesting = !project.hasProperty("nointegration") && (!isBeta || enableTestClient)

val javaTargetVersion = JavaVersion.VERSION_1_8
val javaVersion = JavaVersion.current()
println("Current version: $version (unstable: $isBeta) Game: $game (Kotlin ${kotlinExtension.coreLibrariesVersion}, Java $javaVersion)")
if (javaVersion != javaTargetVersion)
    System.err.println("Java version $javaTargetVersion is recommended - expect issues with generating documentation (consider '-x doc' if you don't care)")

val doAfterEvaluate = ArrayList<(Project) -> Unit>()
tasks {
    val startServer by creating {
        dependsOn(":server:run")
        group = "application"
    }
    
    val doc by creating(DokkaTask::class) {
        dependsOn(documentedProjects.map { ":$it:classes" })
        group = "documentation"
        outputDirectory = bundleDir.resolve("doc").toString()
        outputFormat = "javadoc"
        subProjects = documentedProjects
        configuration {
            reportUndocumented = false
            moduleName = "Software-Challenge API $version"
            jdkVersion = 8
        }
    }
    
    val bundle by creating {
        dependsOn(doc)
        dependOnSubprojects()
        group = "distribution"
        description = "Zips everything up for release into ${bundleDir.relativeTo(projectDir)}"
        outputs.dir(bundleDir)
    }
    
    val release by creating {
        dependsOn(clean, check)
        group = "distribution"
        description = "Prepares a new Release by bumping the version and pushing a commit tagged with the new version"
        doLast {
            var newVersion = version
            fun String.editVersion(version: String, new: Int) =
                    if (startsWith("socha.version.$version"))
                        "socha.version.$version=${new.toString().padStart(2, '0')}"
                    else this
            val versionLineUpdater: (String) -> String = when {
                project.hasProperty("manual") -> ({ it })
                project.hasProperty("minor") -> ({
                    newVersion = "${versionObject.major}.${versionObject.minor + 1}.0"
                    it.editVersion("minor", versionObject.minor + 1).editVersion("patch", 0)
                })
                project.hasProperty("patch") -> ({
                    newVersion = "${versionObject.major}.${versionObject.minor}.${versionObject.patch + 1}"
                    it.editVersion("patch", versionObject.patch + 1)
                })
                else -> throw InvalidUserDataException("Gib entweder -Ppatch oder -Pminor an, um die Versionsnummer automatisch zu inkrementieren, oder ändere sie selbst in gradle.properties und gib dann -Pmanual an!")
            }
            
            val desc = project.properties["m"]?.toString()
                       ?: throw InvalidUserDataException("Das Argument -Pm=\"Beschreibung dieser Version\" wird benötigt")
            
            val propsFile = file("gradle.properties")
            propsFile.writeText(propsFile.readLines().joinToString("\n") { versionLineUpdater(it) })
            
            println("Version: $newVersion")
            println("Beschreibung: $desc")
            exec { commandLine("git", "add", "gradle.properties", "CHANGELOG.md") }
            exec { commandLine("git", "commit", "-m", "release: v$newVersion") }
            exec { commandLine("git", "tag", newVersion, "-m", desc) }
            exec { commandLine("git", "push", "--follow-tags") }
        }
    }
    
    clean {
        dependOnSubprojects()
    }
    test {
        dependOnSubprojects()
    }
    build {
        dependsOn(bundle)
    }
    
    // TODO create a global constant which can be shared with testclient & co - maybe a resource?
    val maxGameLength = 150L // 2m30s
    
    val testGame by creating {
        dependsOn(":server:makeRunnable", ":player:bundleShadow")
        group = "verification"
        doFirst {
            val testGameDir = testingDir.resolve("game")
            testGameDir.deleteRecursively()
            testGameDir.mkdirs()
            val java = "java"
                    //File("/usr/lib/jvm").listFiles { f:File -> f.name.contains("java-1") }?.max()?.resolve("bin/java").toString()
            val port = "13054"
            println("Running on Port: $port")
            
            val jarTask = project(":server").getTasksByName("jar", false).single() as Jar
            val server =
                ProcessBuilder(
                    java,
                    "-Dlogback.configurationFile=${project(":server").projectDir.resolve("configuration/logback-trace.xml")}",
                    "-jar", jarTask.archiveFile.get().asFile.absolutePath,
                    "--port", port
                )
                    .redirectOutput(testGameDir.resolve("server.log"))
                    .redirectError(testGameDir.resolve("server-err.log"))
                    .directory(jarTask.destinationDirectory.get().asFile)
                    .start()
            var i = 0
            while(Files.size(testGameDir.resolve("server.log").toPath()) < 1000 && i++ < 50)
                Thread.sleep(300)
            val startClient: (Int) -> Process = {
                Thread.sleep(300)
                ProcessBuilder(
                    java, "-jar", bundleDir.resolve(bundledPlayer).absolutePath,
                    "--port", port
                ).redirectOutput(testGameDir.resolve("client$it.log")).redirectError(testGameDir.resolve("client$it-err.log")).start()
            }
            startClient(1)
            startClient(2)
            val timeout = AtomicBoolean(false)
            val thread = Thread {
                try {
                    Thread.sleep(maxGameLength * 1000)
                } catch (e: InterruptedException) {
                    return@Thread
                }
                timeout.set(true)
                println("Killing Server because of timeout!")
                server.destroyForcibly()
            }.apply {
                isDaemon = true
                start()
            }
            try {
                for (i in 1..2) {
                    val logFile = testGameDir.resolve("client$i.log")
                    var log: String
                    println("Waiting for client $i to receive game result")
                    do {
                        if (!server.isAlive) {
                            if (timeout.get())
                                throw TimeoutException("$this task exceeded $maxGameLength seconds")
                            throw Exception("Server terminated unexpectedly!")
                        }
                        Thread.yield()
                        Thread.sleep(100)
                        log = logFile.readText()
                    } while (!log.contains("stop", true))
                    if (!log.contains("Received game result"))
                        throw Exception("Client $i did not receive the game result - check $logFile")
                }
            } catch (t: Throwable) {
                println("Error in $this - check the logs in $testGameDir")
                throw t
            } finally {
                server.destroy()
            }
            thread.interrupt()
            println("Successfully played a game using the bundled server & client!")
        }
    }
    
    val testTestClient by creating {
        dependsOn(":server:bundle")
        group = "verification"
        shouldRunAfter(testGame)
        val testClientGames = 3
        doFirst {
            testingDir.mkdirs()
            val serverDir = testingDir.resolve("testclient")
            serverDir.deleteRecursively()
            unzipTo(serverDir, bundleDir.resolve("software-challenge-server.zip"))
    
            val command = (project(":test-client").getTasksByName("createStartScripts", false).single() as ScriptsTask).content.split(' ') +
                          arrayOf("--start-server", "--tests", testClientGames.toString(), "--port", "13055")
            println("Testing TestClient with $command")
            val testClient = ProcessBuilder(command).directory(serverDir).start()
            if (testClient.waitFor(maxGameLength * testClientGames, TimeUnit.SECONDS)) {
                val value = testClient.exitValue()
                // TODO check whether TestClient actually played games
                if (value == 0)
                    println("TestClient successfully tested!")
                else
                    throw Exception("TestClient exited with exit code $value - check the logs under $serverDir!")
            } else {
                throw Exception("TestClient exceeded timeout of ${maxGameLength * testClientGames} seconds - check the logs under $serverDir!")
            }
        }
    }
    
    val integrationTest by creating {
        dependsOn(testGame, ":player:playerTest")
        if (enableTestClient)
            dependsOn(testTestClient)
        group = "verification"
        shouldRunAfter(test)
    }
    
    check {
        dependOnSubprojects()
        if (enableIntegrationTesting)
            dependsOn(integrationTest)
    }
}

// == Cross-project configuration ==

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")
    
    dependencies {
        testImplementation(project(":sdk", "testConfig"))
    }
    
    tasks {
        test {
            useJUnitPlatform()
        }
        
        withType<JavaCompile> {
            this.sourceCompatibility = javaTargetVersion.toString()
            this.targetCompatibility = javaTargetVersion.toString()
        }
        
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                this.jvmTarget = javaTargetVersion.toString()
                this.freeCompilerArgs = listOf("-Xjvm-default=all")
            }
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.wso2.org/nexus/content/groups/wso2-public/")
    }
    
    if (this.name in documentedProjects) {
        apply(plugin = "maven")
        apply(plugin = "org.jetbrains.dokka")
        tasks {
            val doc by creating(DokkaTask::class) {
                group = "documentation"
                dependsOn(classes)
                outputDirectory = buildDir.resolve("doc").toString()
                outputFormat = "javadoc"
            }
            val docJar by creating(Jar::class) {
                group = "build"
                from(doc)
                archiveBaseName.set(jar.get().archiveBaseName)
                archiveClassifier.set("javadoc")
            }
            val sourcesJar by creating(Jar::class) {
                group = "build"
                archiveBaseName.set(jar.get().archiveBaseName)
                archiveClassifier.set("sources")
                from(sourceSets.main.get().allSource)
            }
            install {
                dependsOn(docJar, sourcesJar)
            }
            artifacts {
                archives(sourcesJar.archiveFile) { classifier = "sources" }
                archives(docJar.archiveFile) { classifier = "javadoc" }
            }
        }
    }
    
    afterEvaluate {
        doAfterEvaluate.forEach { action -> action(this) }
        tasks {
            forEach { if (!it.name.endsWith("clean", true)) it.mustRunAfter(clean.get()) }
            test { testLogging { showStandardStreams = project.properties["verbose"] != null } }
            withType<Jar> {
                if (plugins.hasPlugin(ApplicationPlugin::class))
                    manifest.attributes(
                            "Main-Class" to project.extensions.getByType<JavaApplication>().mainClass.get(),
                            "Add-Opens" to arrayOf(
                                    // Expose list internals for xstream conversion: https://github.com/x-stream/xstream/issues/253
                                    "java.base/java.util").joinToString(" ")
                    )
            }
        }
    }
}

fun Task.dependOnSubprojects() {
    if (this.project == rootProject)
        doAfterEvaluate.add {
            if (it != rootProject && it.name != "plugin")
                dependsOn(it.tasks.findByName(name) ?: return@add)
        }
}
