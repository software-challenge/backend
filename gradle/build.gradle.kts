import org.gradle.internal.os.OperatingSystem
import org.jetbrains.dokka.gradle.DokkaTask
import java.io.InputStream
import java.util.concurrent.TimeUnit

plugins {
    maven
    `java-library`
    kotlin("jvm") version "1.3.21"
    id("com.github.ben-manes.versions") version "0.21.0"
    id("org.jetbrains.dokka") version "0.9.17"
}

val year: String by project
val gameName: String by project
val game by extra { "${gameName}_$year" }
version = year.substring(2) + "." + property("socha.version")
println("Current version: $version Game: $game")

val deployDir by extra { buildDir.resolve("deploy") }
val deployedClient: String by extra { deployDir.resolve("simpleclient-$gameName-$version.jar").absolutePath }

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
}

val doAfterEvaluate = ArrayList<(Project) -> Unit>()
val mainGroup = "_main"
tasks {
    val startServer by creating {
        dependsOn(":server:run")
        group = mainGroup
    }

    val doc by creating(DokkaTask::class) {
        moduleName = "Software-Challenge API $version"
        val sourceSets = arrayOf("sdk", "plugin").map { project(it).sourceSets.main.get() }
        sourceDirs = files(sourceSets.map { it.java.sourceDirectories })
        outputDirectory = deployDir.resolve("doc").toString()
        outputFormat = "javadoc"
        jdkVersion = 8
        doFirst {
            classpath = files(sourceSets.map { it.runtimeClasspath }.flatMap { it.files }.filter { it.exists() })
        }
    }

    val deploy by creating {
        dependsOn(doc)
        dependOnSubprojects()
        group = mainGroup
        description = "Zips everything up for release into ./build/deploy"
    }

    val release by creating {
        dependsOn(deploy)
        group = mainGroup
        description = "Prepares a new Release by bumping the version and creating a commit and a git tag"
        doLast {
            val v = project.properties["v"]?.toString()?.takeIf { it.count { char -> char == '.' } == 1 }
                    ?: throw InvalidUserDataException("Die Flag -Pv=\"Version\" wird im Format X.X benötigt")
            val desc = project.properties["desc"]?.toString()
                    ?: throw InvalidUserDataException("Die Flag -Pdesc=\"Beschreibung dieser Version\" wird benötigt")
            val version = "${year.substring(2)}.$v"
            println("Version: $version")
            println("Beschreibung: $desc")
            file("gradle.properties").writeText(file("gradle.properties").readText()
                    .replace(Regex("socha.version.*"), "socha.version = $v"))
            exec { commandLine("git", "add", "gradle.properties") }
            exec { commandLine("git", "commit", "-m", version) }
            exec { commandLine("git", "tag", version, "-m", desc) }
            exec { commandLine("git", "push", "--follow-tags") }
            println("""
            ===================================================
            Fertig! Jetzt noch folgende Schritte ausfuehren:
             - ein Release für die GUI erstellen
             - auf der Website (http://www.software-challenge.de/wp-admin) unter Medien die Dateien ersetzen
             - unter Seiten die Downloadseite aktualisieren (neue Version in Versionshistorie eintragen)

            Dann auf der Wettkampfseite (http://contest.software-challenge.de) was unter Aktuelles schreiben:

            Eine neue Version der Software ist verfügbar: $desc
            Dafür gibt es einen neuen Server und Simpleclient im [Download-Bereich der Website][1].

            [1]: http://www.software-challenge.de/downloads/

            Dann noch etwas im Discord-Server in #news schreiben:
            Good news @everyone! Neue Version der Software: http://www.software-challenge.de/downloads/
            Highlights: $desc
            ===================================================""".trimIndent())
        }
    }

    val testDeployed by creating {
        dependsOn(deploy)
        group = mainGroup
        val testDir = buildDir.resolve("tests")
        doLast {
            val gameWaitSeconds = 150L
            val testClientGames = 3

            testDir.deleteRecursively()
            testDir.mkdirs()
            val server = ProcessBuilder("java", "-Dlogback.configurationFile=logback.xml", "-jar",
                    project("server").tasks.jar.get().archiveFile.get().asFile.absolutePath)
                    .redirectOutput(testDir.resolve("server.log")).redirectError(testDir.resolve("server-err.log"))
                    .directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(1000)
            val startClient: (Int) -> Process = {
                ProcessBuilder("java", "-jar", deployedClient)
                        .redirectOutput(testDir.resolve("client$it.log")).redirectError(testDir.resolve("client$it-err.log")).start()
            }
            startClient(1)
            startClient(2)
            val thread = Thread {
                try {
                    Thread.sleep(gameWaitSeconds * 1000)
                } catch(e: InterruptedException) {
                    return@Thread
                }
                println("testDeployed has been running for over $gameWaitSeconds seconds - killing server!")
                server.destroyForcibly()
            }.apply {
                isDaemon = true
                start()
            }
            try {
                for(i in 1..2) {
                    println("Waiting for client $i to receive game result")
                    do {
                        if(!server.isAlive)
                            throw Exception("Server terminated unexpectedly!")
                        Thread.sleep(200)
                    } while(!testDir.resolve("client$i.log").readText().contains("Received game result", true))
                }
            } catch(t: Throwable) {
                println("Error in testDeployed - check the logs in $testDir")
                throw t
            } finally {
                server.destroy()
            }
            thread.interrupt()
            println("Successfully played a game using the deployed server & client!")

            val unzipped = deployDir.resolve("software-challenge-server")
            unzipped.deleteRecursively()
            Runtime.getRuntime().exec("unzip software-challenge-server.zip -d $unzipped", null, deployDir).waitFor()

            println("Testing TestClient...")
            val testClient = ProcessBuilder(
                    project("test-client").tasks.getByName("createScripts", ScriptsTask::class).content.split(" ") +
                            listOf("--start-server", "--tests", "$testClientGames"))
                    .redirectOutput(testDir.resolve("test-client.log")).redirectError(testDir.resolve("test-client-err.log"))
                    .directory(unzipped).start()
            if(testClient.waitFor(gameWaitSeconds * testClientGames, TimeUnit.SECONDS)) {
                val value = testClient.exitValue()
                if(value == 0)
                    println("TestClient successfully tested!")
                else
                    throw Exception("TestClient exited with exit code $value!")
            } else {
                throw Exception("TestClient exceeded timeout of ${gameWaitSeconds * testClientGames} seconds!")
            }
        }
    }

    clean {
        dependOnSubprojects()
        group = mainGroup
    }
    test {
        dependOnSubprojects()
        dependsOn(testDeployed)
        group = mainGroup
    }
    build {
        group = mainGroup
    }
    replace("run").dependsOn(testDeployed)
}

// == Cross-project configuration ==

allprojects {
    repositories {
        jcenter()
        maven("http://dist.wso2.org/maven2")
    }
    if(this.name in arrayOf("sdk", "plugin")) {
        apply(plugin = "maven")
        tasks {
            val doc by creating(DokkaTask::class) {
                moduleName = "Software-Challenge API $version"
                classpath = sourceSets.main.get().runtimeClasspath
                outputDirectory = buildDir.resolve("doc").toString()
                outputFormat = "javadoc"
                jdkVersion = 8
                doFirst {
                    classpath = files(sourceSets.main.get().runtimeClasspath.files.filter { it.exists() })
                }
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
        doAfterEvaluate.forEach { it(this) }
        tasks {
            forEach { if(it.name != clean.name) it.mustRunAfter(clean.get()) }
            test {
                testLogging { showStandardStreams = project.properties["verbose"] != null }
            }
            withType<Jar> {
                if(plugins.hasPlugin(ApplicationPlugin::class))
                    manifest.attributes["Main-Class"] = project.extensions.getByType<JavaApplication>().mainClassName
            }
        }
    }
}

project("sdk") {
    sourceSets.main.get().java.srcDirs("src/framework", "src/server-api")

    dependencies {
        api(kotlin("stdlib"))
        api("com.thoughtworks.xstream", "xstream", "1.4.11.1")
        api("jargs", "jargs", "1.0")
        api("ch.qos.logback", "logback-classic", "1.2.3")

        implementation("org.hamcrest", "hamcrest-core", "2.1")
        implementation("net.sf.kxml", "kxml2", "2.3.0")
        implementation("xmlpull", "xmlpull", "1.1.3.1")
    }
}

project("plugin") {
    sourceSets {
        main.get().java.srcDirs("src/client", "src/server", "src/shared")
        test.get().java.srcDir("src/test")
    }

    dependencies {
        api(project(":sdk"))

        testImplementation("junit", "junit", "4.12")
    }

    tasks.jar.get().archiveBaseName.set(game)
}

// == Utilities ==

fun InputStream.dump(name: String? = null) {
    if(name != null)
        println("\n$name:")
    while(available() > 0)
        print(read().toChar())
    close()
}

fun Task.dependOnSubprojects() {
    if(this.project == rootProject)
        doAfterEvaluate.add {
            if(it != rootProject)
                dependsOn(it.tasks.findByName(name) ?: return@add)
        }
}

// "run" task won't work when recursive, see https://stackoverflow.com/q/51903863/6723250
gradle.taskGraph.whenReady {
    val hasRootRunTask = hasTask(":run")
    if(hasRootRunTask) {
        allTasks.forEach { task ->
            task.enabled = task.name != "run"
        }
    }
}
