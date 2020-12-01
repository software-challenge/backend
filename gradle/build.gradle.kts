import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import sc.gradle.ScriptsTask
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

plugins {
    maven
    `java-library`
    kotlin("jvm") version "1.4.20"
    id("org.jetbrains.dokka") version "0.10.1"
    id("scripts-task")
    
    id("com.github.ben-manes.versions") version "0.36.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
}

val gameName by extra { property("socha.gameName") as String }
val versions = arrayOf("year", "minor", "patch").map { property("socha.version.$it").toString().toInt() }
val versionObject = KotlinVersion(versions[0], versions[1], versions[2])
version = versions.joinToString(".") { it.toString() }
val year by extra { "20${versionObject.major}" }
val game by extra { "${gameName}_$year" }

val javaTargetVersion = JavaVersion.VERSION_1_8
val javaVersion = JavaVersion.current()
println("Current version: $version Game: $game (Java version: $javaVersion)")
if(javaVersion != javaTargetVersion)
    System.err.println("Java version $javaTargetVersion is the recommended version - expect issues with generating documentation (consider using '-x doc' if you don't care)")

val deployDir by extra { buildDir.resolve("deploy") }
val deployedPlayer by extra { "simpleclient-$gameName-$version.jar" }
val testLogDir by extra { buildDir.resolve("tests") }
val documentedProjects = arrayOf("sdk", "plugin")

val enableTestClient by extra { versionObject.minor > 0 }
val enableIntegrationTesting = !project.hasProperty("nointegration") && (versionObject.minor > 0 || enableTestClient)

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")
    
    dependencies {
        testImplementation("io.kotest", "kotest-runner-junit5-jvm", "4.3.1")
        testImplementation("io.kotest", "kotest-assertions-core", "4.3.1")
    }
    
    tasks {
        test {
            useJUnitPlatform()
        }
    }
}

val doAfterEvaluate = ArrayList<(Project) -> Unit>()
tasks {
    val startServer by creating {
        dependsOn(":server:run")
        group = "application"
    }
    
    val doc by creating(DokkaTask::class) {
        logging.level = LogLevel.QUIET
        outputDirectory = deployDir.resolve("doc").toString()
        outputFormat = "javadoc"
        subProjects = listOf("sdk", "plugin")
        configuration {
            reportUndocumented = false
            moduleName = "Software-Challenge API $version"
            jdkVersion = 8
        }
    }
    
    val deploy by creating {
        dependsOn(doc)
        dependOnSubprojects()
        group = "distribution"
        description = "Zips everything up for release into ${deployDir.relativeTo(projectDir)}"
        outputs.dir(deployDir)
    }
    
    val release by creating {
        dependsOn(check)
        group = "distribution"
        description = "Prepares a new Release by bumping the version and creating a commit with a git tag of the new version"
        doLast {
            fun edit(original: String, version: String, new: Int) =
                    if (original.startsWith("socha.version.$version"))
                        "socha.version.$version=${new.toString().padStart(2, '0')}"
                    else original
            
            var newVersion = version
            val filter: (String) -> String = when {
                project.hasProperty("manual") -> ({ it })
                project.hasProperty("minor") -> ({
                    newVersion = "${versionObject.major}.${versionObject.minor + 1}.0"
                    edit(edit(it, "minor", versionObject.minor + 1), "patch", 0)
                })
                project.hasProperty("patch") -> ({
                    newVersion = "${versionObject.major}.${versionObject.minor}.${versionObject.patch + 1}"
                    edit(it, "patch", versionObject.patch + 1)
                })
                else -> throw InvalidUserDataException("Gib entweder -Ppatch oder -Pminor an, um die Versionsnummer automatisch zu inkrementieren, oder ändere sie selbst in gradle.properties und gib dann -Pmanual an!")
            }
            val desc = project.properties["m"]?.toString()
                    ?: throw InvalidUserDataException("Das Argument -Pm=\"Beschreibung dieser Version\" wird benötigt")
            
            val propsFile = file("gradle.properties")
            propsFile.writeText(propsFile.readLines().joinToString("\n") { filter(it) })
            
            println("Version: $newVersion")
            println("Beschreibung: $desc")
            exec { commandLine("git", "add", "gradle.properties") }
            exec { commandLine("git", "commit", "-m", "release: $newVersion") }
            exec { commandLine("git", "tag", newVersion, "-m", desc) }
            exec { commandLine("git", "push", "--follow-tags") }
        }
    }
    
    val maxGameLength = 150L
    
    val clearTestLogs by creating(Delete::class) {
        delete(testLogDir)
    }
    
    val testGame by creating {
        group = "verification"
        dependsOn(clearTestLogs, ":server:deploy", ":player:deploy")
        doFirst {
            testLogDir.mkdirs()
            val server = ProcessBuilder("java",
                    "-Dlogback.configurationFile=${project("server").projectDir.resolve("configuration/logback-trace.xml")}",
                    "-jar", project("server").tasks.jar.get().archiveFile.get().asFile.absolutePath)
                    .redirectOutput(testLogDir.resolve("server.log")).redirectError(testLogDir.resolve("server-err.log"))
                    .directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(400)
            val startClient: (Int) -> Process = {
                Thread.sleep(100)
                ProcessBuilder("java", "-jar", deployDir.resolve(deployedPlayer).absolutePath)
                        .redirectOutput(testLogDir.resolve("client$it.log")).redirectError(testLogDir.resolve("client$it-err.log")).start()
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
                println("$this has been running for over $maxGameLength seconds - killing server!")
                server.destroyForcibly()
            }.apply {
                isDaemon = true
                start()
            }
            try {
                for (i in 1..2) {
                    val logFile = testLogDir.resolve("client$i.log")
                    var log: String
                    println("Waiting for client $i to receive game result")
                    do {
                        if (!server.isAlive) {
                            if (!timeout.get())
                                throw Exception("Server terminated unexpectedly!")
                            return@doFirst
                        }
                        Thread.yield()
                        Thread.sleep(100)
                        log = logFile.readText()
                    } while (!log.contains("stop", true))
                    if (!log.contains("Received game result"))
                        throw Exception("Client $i did not receive the game result - check $logFile")
                }
            } catch (t: Throwable) {
                println("Error in $this - check the logs in $testLogDir")
                throw t
            } finally {
                server.destroy()
            }
            thread.interrupt()
            println("Successfully played a game using the deployed server & client!")
        }
    }
    
    val testTestClient by creating {
        group = "verification"
        dependsOn(clearTestLogs, ":server:deploy")
        shouldRunAfter(testGame)
        val testClientGames = 3
        doFirst {
            val tmpDir = buildDir.resolve("tmp")
            tmpDir.mkdirs()
            testLogDir.mkdirs()
            val unzipped = tmpDir.resolve("software-challenge-server")
            unzipped.deleteRecursively()
            Runtime.getRuntime().exec("unzip software-challenge-server.zip -d $unzipped", null, deployDir).waitFor()
            
            println("Testing TestClient...")
            val testClient = ProcessBuilder(
                    project("test-client").tasks.getByName<ScriptsTask>("createScripts").content.split(" ") +
                            listOf("--start-server", "--tests", testClientGames.toString(), "--port", "13055"))
                    .redirectOutput(testLogDir.resolve("test-client.log")).redirectError(testLogDir.resolve("test-client-err.log"))
                    .directory(unzipped).start()
            if (testClient.waitFor(maxGameLength * testClientGames, TimeUnit.SECONDS)) {
                val value = testClient.exitValue()
                if (value == 0)
                    println("TestClient successfully tested!")
                else
                    throw Exception("TestClient exited with exit code $value!")
            } else {
                throw Exception("TestClient exceeded timeout of ${maxGameLength * testClientGames} seconds!")
            }
        }
    }
    
    val integrationTest by creating {
        group = "verification"
        dependsOn(testGame, ":player:playerTest")
        if(enableTestClient)
            dependsOn(testTestClient)
        shouldRunAfter(test)
    }
    
    clean {
        dependOnSubprojects()
    }
    test {
        dependOnSubprojects()
    }
    check {
        if (enableIntegrationTesting)
            dependsOn(integrationTest)
    }
    build {
        dependsOn(deploy)
    }
}

// == Cross-project configuration ==

allprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions{
            jvmTarget = javaTargetVersion.toString()
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
    
    repositories {
        jcenter()
        maven("http://dist.wso2.org/maven2")
    }
    
    if (this.name in documentedProjects) {
        apply(plugin = "maven")
        apply(plugin = "org.jetbrains.dokka")
        tasks {
            val doc by creating(DokkaTask::class) {
                logging.level = LogLevel.QUIET
                outputDirectory = buildDir.resolve("doc").toString()
                outputFormat = "javadoc"
            }
            val docJar by creating(Jar::class) {
                dependsOn(doc)
                archiveBaseName.set(jar.get().archiveBaseName)
                archiveClassifier.set("javadoc")
                from(doc.outputDirectory)
            }
            val sourcesJar by creating(Jar::class) {
                archiveBaseName.set(jar.get().archiveBaseName)
                archiveClassifier.set("sources")
                from(sourceSets.main.get().allSource)
            }
            install.get().dependsOn(docJar, sourcesJar)
            artifacts {
                archives(sourcesJar.archiveFile) { classifier = "sources" }
                archives(docJar.archiveFile) { classifier = "javadoc" }
            }
        }
    }
    
    afterEvaluate {
        doAfterEvaluate.forEach { action -> action(this) }
        tasks {
            forEach { if (it.name != clean.name) it.mustRunAfter(clean.get()) }
            test {
                testLogging { showStandardStreams = project.properties["verbose"] != null }
            }
            withType<Jar> {
                if (plugins.hasPlugin(ApplicationPlugin::class))
                    manifest.attributes["Main-Class"] = project.extensions.getByType<JavaApplication>().mainClassName
            }
        }
    }
}

// == Utilities ==

fun InputStream.dump(name: String? = null) {
    if (name != null)
        println("\n$name:")
    while (available() > 0)
        print(read().toChar())
    close()
}

fun Task.dependOnSubprojects() {
    if (this.project == rootProject)
        doAfterEvaluate.add {
            if (it != rootProject)
                dependsOn(it.tasks.findByName(name) ?: return@add)
        }
}

// "run" task won't work when recursive, see https://stackoverflow.com/q/51903863/6723250
gradle.taskGraph.whenReady {
    val hasRootRunTask = hasTask(":run")
    if (hasRootRunTask) {
        allTasks.forEach { task ->
            task.enabled = task.name != "run"
        }
    }
}
