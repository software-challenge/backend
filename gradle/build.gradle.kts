import org.gradle.kotlin.dsl.support.unzipTo
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import sc.gradle.ScriptsTask
import java.util.concurrent.atomic.AtomicBoolean

plugins {
    kotlin("jvm") version "1.4.30"
    id("org.jetbrains.dokka") version "0.10.1"
    id("scripts-task")
    `maven-publish`
    
    id("com.github.ben-manes.versions") version "0.38.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
}

val gameName by extra { property("socha.gameName") as String }
val versions = arrayOf("year", "minor", "patch").map { property("socha.version.$it").toString().toInt() }
val versionObject = KotlinVersion(versions[0], versions[1], versions[2])
version = versionObject.toString()
val year by extra { "20${versionObject.major}" }
val game by extra { "${gameName}_$year" }

val javaTargetVersion = JavaVersion.VERSION_1_8
val javaVersion = JavaVersion.current()
println("Current version: $version Game: $game (Java version: $javaVersion)")
if (javaVersion != javaTargetVersion)
    System.err.println("Java version $javaTargetVersion is recommended - expect issues with generating documentation (consider using '-x doc' if you don't care)")

val deployDir by extra { buildDir.resolve("deploy") }
val deployedPlayer by extra { "simpleclient-$gameName-$version.jar" }
val testingDir by extra { buildDir.resolve("tests") }
val documentedProjects = listOf("sdk", "plugin")

val enableTestClient by extra { versionObject.minor > 0 }
val enableIntegrationTesting = !project.hasProperty("nointegration") && (versionObject.minor > 0 || enableTestClient)

val doAfterEvaluate = ArrayList<(Project) -> Unit>()
tasks {
    val startServer by creating {
        group = "application"
        dependsOn(":server:run")
    }
    
    val doc by creating(DokkaTask::class) {
        group = "documentation"
        dependsOn(documentedProjects.map { ":$it:classes" })
        outputDirectory = deployDir.resolve("doc").toString()
        outputFormat = "javadoc"
        subProjects = documentedProjects
        configuration {
            reportUndocumented = false
            moduleName = "Software-Challenge API $version"
            jdkVersion = 8
        }
    }
    
    val deploy by creating {
        group = "distribution"
        dependsOn(doc)
        dependOnSubprojects()
        description = "Zips everything up for release into ${deployDir.relativeTo(projectDir)}"
        outputs.dir(deployDir)
    }
    
    val release by creating {
        group = "distribution"
        dependsOn(check)
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
    
    clean {
        dependOnSubprojects()
    }
    test {
        dependOnSubprojects()
    }
    build {
        dependsOn(deploy)
    }
    
    // TODO create a global constant which can be shared with testclient & co - maybe a resource?
    val maxGameLength = 150L
    
    val testGame by creating {
        group = "verification"
        dependsOn(":server:deploy", ":player:deploy")
        doFirst {
            val testGameDir = testingDir.resolve("game")
            testGameDir.deleteRecursively()
            testGameDir.mkdirs()
            val server =
                ProcessBuilder(
                    "java",
                    "-Dlogback.configurationFile=${project(":server").projectDir.resolve("configuration/logback-trace.xml")}",
                    "-jar", (project(":server").getTasksByName("jar", false).single() as Jar).archiveFile.get().asFile.absolutePath
                )
                    .redirectOutput(testGameDir.resolve("server.log"))
                    .redirectError(testGameDir.resolve("server-err.log"))
                    .directory(project(":server").buildDir.resolve("runnable"))
                    .start()
            Thread.sleep(400)
            val startClient: (Int) -> Process = {
                Thread.sleep(100)
                ProcessBuilder("java", "-jar", deployDir.resolve(deployedPlayer).absolutePath)
                    .redirectOutput(testGameDir.resolve("client$it.log")).redirectError(testGameDir.resolve("client$it-err.log")).start()
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
                    val logFile = testGameDir.resolve("client$i.log")
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
                println("Error in $this - check the logs in $testGameDir")
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
        dependsOn(":server:deploy")
        shouldRunAfter(testGame)
        val testClientGames = 3
        doFirst {
            testingDir.mkdirs()
            val serverDir = testingDir.resolve("testclient")
            serverDir.deleteRecursively()
            unzipTo(serverDir, deployDir.resolve("software-challenge-server.zip"))
    
            println("Testing TestClient...")
            val testClient =
                    ProcessBuilder(
                            (project(":test-client").getTasksByName("createStartScripts", false).single() as ScriptsTask).content.split(' ') +
                            arrayOf("--start-server", "--tests", testClientGames.toString(), "--port", "13055")
                    )
                            .directory(serverDir)
                            .start()
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
        group = "verification"
        dependsOn(testGame, ":player:playerTest")
        if (enableTestClient)
            dependsOn(testTestClient)
        shouldRunAfter(test)
    }
    
    check {
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
        
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = javaTargetVersion.toString()
                freeCompilerArgs = listOf("-Xjvm-default=all")
            }
        }
    }
}

allprojects {
    repositories {
        jcenter()
        maven("http://dist.wso2.org/maven2")
    }
    
    if (this.name in documentedProjects) {
        apply(plugin = "maven-publish")
        apply(plugin = "org.jetbrains.dokka")
        publishing {
            publications {
                create<MavenPublication>(name) {
                    println(components.joinToString())
                    from(components["java"])
                    version = rootProject.version.toString()
                }
            }
        }
        java {
            withSourcesJar()
            withJavadocJar()
        }
        tasks {
            val doc by creating(DokkaTask::class) {
                group = "documentation"
                dependsOn(classes)
                outputDirectory = buildDir.resolve("doc").toString()
                outputFormat = "javadoc"
            }
            val docJar by creating(Jar::class) {
                group = "build"
                dependsOn(doc)
                archiveBaseName.set(jar.get().archiveBaseName)
                archiveClassifier.set("javadoc")
                from(doc.outputDirectory)
            }
            //val sourcesJar by creating(Jar::class) {
            //    group = "build"
            //    archiveBaseName.set(jar.get().archiveBaseName)
            //    archiveClassifier.set("sources")
            //    from(sourceSets.main.get().allSource)
            //}
            artifacts {
                //archives(sourcesJar.archiveFile) { classifier = "sources" }
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
                    manifest.attributes["Main-Class"] = project.extensions.getByType<JavaApplication>().mainClass.get()
            }
        }
    }
}

// == Utilities ==

fun Task.dependOnSubprojects() {
    if (this.project == rootProject)
        doAfterEvaluate.add {
            if (it != rootProject)
                dependsOn(it.tasks.findByName(name) ?: return@add)
        }
}
