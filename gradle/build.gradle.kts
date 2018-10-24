import org.gradle.internal.os.OperatingSystem
import java.io.InputStream

plugins {
    maven
    `java-library`
    kotlin("jvm") version "1.2.61"
    id("com.github.ben-manes.versions") version "0.19.0"
}

val year = property("socha.year").toString()
val gameName = property("socha.gameName").toString()
val game = "${gameName}_$year"
version = year.substring(2) + "." + property("socha.version").toString()
project.ext.set("game", game)
println("Current version: $version  Game: $game")

val deployDir = buildDir.resolve("deploy")
project.ext.set("deployDir", deployDir)

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
}

val doAfterEvaluate = ArrayList<(Project) -> Unit>()
val mainGroup = "_main"
tasks {
    create("startServer") {
        dependsOn(":server:run")
        group = mainGroup
    }

    create("deploy") {
        dependsOn("clean", "doc")
        dependOnSubprojects()
        group = mainGroup
        description = "Zips everything up for release into build/deploy"
    }

    "clean"(Delete::class) {
        delete(allprojects.map { it.buildDir })
        group = mainGroup
    }

    create("release") {
        dependsOn("deploy")
        group = mainGroup
        description = "Prepares a new Release by bumping the version and creating a commit and a git tag"
        doLast {
            val v = properties["v"]?.toString()?.takeIf { it.count { char -> char == '.' } == 1 }
                    ?: throw InvalidUserDataException("Die Flag -Pv=\"Version\" wird im Format X.X benötigt")
            val desc = properties["desc"]?.toString()
                    ?: throw InvalidUserDataException("Die Flag -Pdesc=\"Beschreibung dieser Version\" wird benötigt")
            val version = "${year.substring(2)}.$v"
            println("Version: $version")
            println("Beschreibung: $desc")
            file("gradle.properties").writeText(file("gradle.properties").readText().replace(Regex("socha.version.*"), "socha.version = $v"))
            exec { commandLine("git", "add", "gradle.properties") }
            exec { commandLine("git", "commit", "-m", version) }
            exec { commandLine("git", "tag", version, "-m", desc) }
            exec { commandLine("git", "push", "--tags") }
            println("""
    ===================================================
    Fertig! Jetzt noch folgende Schritte ausfuehren:
     - einen Release für die GUI erstellen
     - auf der Website (http://www.software-challenge.de/wp-admin) unter Medien die Dateien ersetzen
     - unter Seiten die Downloadseite aktualisieren (neue Version in Versionshistorie eintragen)

    Dann auf der Wettkampfseite (http://contest.software-challenge.de) was unter Aktuelles schreiben:

    Eine neue Version der Software ist verfügbar: $desc
    Dafür gibt es einen neuen Server und Simpleclient im [Download-Bereich der Website][1].

    [1]: http://www.software-challenge.de/downloads/

    Dann noch etwas im Discord-Chat in #news schreiben:
    Good news @everyone! Neue Version der Software: http://www.software-challenge.de/downloads/
    Highlights: $desc
    ===================================================""".trimIndent())
        }
    }

    create("testDeployed") {
        dependsOn("deploy")
        group = mainGroup
        val testDir = buildDir.resolve("test")
        doLast {
            testDir.mkdirs()
            val server = ProcessBuilder("java", "-Dlogback.configurationFile=logback.xml", "-jar", project("server").tasks["jar"].outputs.files.first().absolutePath)
                    .redirectOutput(testDir.resolve("server.log")).redirectError(testDir.resolve("server-err.log"))
                    .directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(300)
            val startClient: (Int) -> Process = {
                ProcessBuilder("java", "-jar", deployDir.resolve("simpleclient-$gameName-$version.jar").absolutePath)
                        .redirectOutput(testDir.resolve("client$it.log")).redirectError(testDir.resolve("client$it-err.log")).start()
            }
            startClient(1)
            startClient(2)
            Thread {
                Thread.sleep(150_000)
                println("testDeployed has been running for over 150 seconds - interrupting!")
                server.destroy()
            }.run {
                isDaemon = true
                start()
            }
            try {
                for (i in 1..2) {
                    println("Waiting for client $i to receive game result")
                    do {
                        if (!server.isAlive)
                            throw Exception("Server terminated unexpectedly!")
                        Thread.sleep(200)
                    } while (!testDir.resolve("client$i.log").readText().contains("Received game result", true))
                }
            } catch (t: Throwable) {
                println("Error in testDeployed - check the logs in $testDir")
                throw t
            } finally {
                server.destroy()
            }
            println("Successfully played a game using the deployed server & client!")
        }
    }

    create<Javadoc>("doc") {
        title = "Software-Challenge API $version"
        val sourceSets = arrayOf("plugin", "sdk").map { project(it).sourceSets.getByName("main") }
        source(sourceSets.map { it.allJava })
        classpath = files(sourceSets.map { it.runtimeClasspath })
        setDestinationDir(deployDir.resolve("doc"))
    }
    "test" {
        dependsOn("run")
        group = mainGroup
    }
    "build" {
        dependsOn("deploy")
        group = mainGroup
    }
    replace("run").dependsOn("testDeployed")
}

// == Cross-project configuration ==

allprojects {
    repositories {
        jcenter()
        maven("http://dist.wso2.org/maven2")
    }
    if (this.name in arrayOf("sdk", "plugin")) {
        apply(plugin = "maven")
        tasks {
            val sourcesJar by creating(Jar::class) {
                baseName = tasks.getByName<Jar>("jar").baseName
                classifier = "sources"
                from(sourceSets.getByName("main").allSource)
            }
            val javadocJar by creating(Jar::class) {
                baseName = tasks.getByName<Jar>("jar").baseName
                classifier = "javadoc"
                from(tasks.getByName<Javadoc>("javadoc").destinationDir)
            }
            getByName("install").dependsOn("javadocJar", "sourcesJar")
            artifacts {
                add("archives", sourcesJar.outputs.files.first()) { classifier = "sources" }
                add("archives", javadocJar.outputs.files.first()) { classifier = "javadoc" }
            }
        }
    }
    afterEvaluate {
        doAfterEvaluate.forEach { it(this) }
        tasks {
            forEach { if (it.name != "clean") it.mustRunAfter("clean") }
            withType<Javadoc> {
                val silenceDoc = buildDir.resolve("tmp").resolve("silence")
                doFirst { silenceDoc.apply { parentFile.mkdirs(); writeText("-Xdoclint:none -encoding UTF-8 -charset UTF-8 -docencoding UTF-8") } }
                options.optionFiles!!.add(silenceDoc)
            }
            withType<Test> {
                testLogging { showStandardStreams = properties["verbose"] != null }
            }
            withType<Jar> {
                if (plugins.hasPlugin("application"))
                    manifest.attributes["Main-Class"] = ((this@allprojects as org.gradle.api.plugins.ExtensionAware).extensions.getByName("application") as org.gradle.api.plugins.JavaApplication).mainClassName
            }
        }
    }
}

project("sdk") {
    sourceSets {
        getByName("main").java.srcDirs("src/framework", "src/server-api")
    }

    dependencies {
        api(kotlin("stdlib"))
        api("com.thoughtworks.xstream", "xstream", "1.4.10")
        api("jargs", "jargs", "1.0")
        api("ch.qos.logback", "logback-classic", "0.9.15")

        implementation("org.hamcrest", "hamcrest-core", "1.3")
        implementation("net.sf.kxml", "kxml2", "2.3.0")
        implementation("xmlpull", "xmlpull", "1.1.3.1")
    }
}

project("plugin") {
    sourceSets {
        getByName("main").java.srcDirs("src/client", "src/server", "src/shared")
        getByName("test").java.srcDir("src/test")
    }

    dependencies {
        api(project(":sdk"))

        testImplementation("junit", "junit", "4.12")
    }

    tasks.getByName<Jar>("jar").baseName = game
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
