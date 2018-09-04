import org.gradle.internal.os.OperatingSystem
import java.util.zip.ZipInputStream

plugins {
    java
    kotlin("jvm") version "1.2.61"
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
    apply(plugin = "java")
    apply(plugin = "kotlin")
}

val mainGroup = "_main"
tasks {
    "startServer" {
        dependsOn(":server:run")
        group = mainGroup
    }

    "deploy" {
        dependsOn("clean", "doc")
        dependOnSubprojects()
        group = mainGroup
        description = "Zips everything up for release into build/deploy"
    }

    "release" {
        dependsOn("deploy")
        group = mainGroup
        description = "Prepares a new Release by bumping the version and creating a commit and a git tag"
        doLast {
            val v = properties["v"]?.toString()?.takeIf { it.count { it == '.' } == 1 }
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

    "testDeployed" {
        dependsOn("deploy")
        group = mainGroup
        doFirst {
            val server = ProcessBuilder("./start." + if (OperatingSystem.current().isWindows) "bat" else "sh").directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(200)
            val client1 = Runtime.getRuntime().exec("java -jar " + deployDir.resolve("simpleclient-$game-$version.jar"))
            val client2 = Runtime.getRuntime().exec("java -jar " + deployDir.resolve("simpleclient-$game-$version.jar"))
            var line = ""
            mapOf("client1" to client1, "client2" to client2).forEach { clientName, process ->
                val reader = process.inputStream.bufferedReader()
                val lines = ArrayList<String>()
                while (!line.contains("Received game result", true)) {
                    if (!server.isAlive)
                        break
                    line = reader.readLine() ?: break
                    lines.add(line)
                }
                if (!server.isAlive || !line.contains("Received game result", true)) {
                    println("server stdin:")
                    println(server.inputStream.readBytes(server.inputStream.available()).joinToString("") { it.toChar().toString() })
                    if (server.isAlive) {
                        println()
                        println("$clientName stdin:")
                        println(lines)
                        println()
                        println("$clientName stderr:")
                        process.errorStream.bufferedReader().forEachLine { println(it) }
                        throw Exception("$clientName did not receive the game result!")
                    } else {
                        throw Exception("Server terminated unexpectedly!")
                    }
                }
            }
            println("Successfully played a game using the deployed server & client!")
            server.destroy()
        }
    }

    "clean" {
        dependOnSubprojects()
        group = mainGroup
    }
    val doc by creating(Javadoc::class) {
        val projects = arrayOf("players", "plugins", "sdk").map { project(it) }
        source(projects.map { it.java.sourceSets.getByName("main").allJava })
        classpath = files(projects.map { it.java.sourceSets.getByName("main").compileClasspath })
        setDestinationDir(deployDir.resolve("doc"))
    }
    "test" {
        //dependsOn("run")
        group = mainGroup
    }
    "build" {
        dependsOn("deploy")
        group = mainGroup
    }
    tasks.replace("run").dependsOn("testDeployed")
    getByName("jar").enabled = false
}

// == Cross-project configuration ==

allprojects {
    repositories {
        maven("http://dist.wso2.org/maven2")
        jcenter()
    }

    tasks.forEach { if (it.name != "clean") it.mustRunAfter("clean") }
    tasks.withType<Javadoc> {
        val silenceDoc = buildDir.resolve("tmp").resolve("silence")
        doFirst { silenceDoc.apply { parentFile.mkdirs(); writeText("-Xdoclint:none -encoding UTF-8 -charset UTF-8 -docencoding UTF-8") } }
        options.optionFiles!!.add(silenceDoc)
    }
    tasks.withType<Test> {
        testLogging { showStandardStreams = System.getProperty("verbose") != null }
    }
}

project("sdk") {
    java.sourceSets {
        "main" {
            java.srcDirs("src/framework", "src/server-api")
        }
    }

    dependencies {
        compile(kotlin("stdlib"))
        compile("org.hamcrest", "hamcrest-core", "1.3")
        compile("jargs", "jargs", "1.0")
        compile("ch.qos.logback", "logback-classic", "0.9.15")

        compile("net.sf.kxml", "kxml2", "2.3.0")
        compile("xmlpull", "xmlpull", "1.1.3.1")
        compile("com.thoughtworks.xstream", "xstream", "1.4.10")
    }

    tasks {
        "jar"(Jar::class) {
            baseName = "software-challenge-sdk"
        }
    }
}

project("plugins") {
    java.sourceSets {
        "main" { java.srcDirs("$game/client", "$game/server", "$game/shared") }
        "test" { java.srcDir("$game/test") }
    }

    dependencies {
        compile(project(":sdk"))
        testCompile("junit", "junit", "4.12")
    }

    tasks {
        "jar"(Jar::class) {
            baseName = game
        }
    }
}

// == Utilities ==

fun Task.dependOnSubprojects() {
    subprojects.forEach {
        it.afterEvaluate {
            dependsOn(it.tasks.findByName(this@dependOnSubprojects.name) ?: return@afterEvaluate)
        }
    }
}

// fix run task to not be recursive, see https://stackoverflow.com/q/51903863/6723250
gradle.taskGraph.whenReady {
    val hasRootRunTask = hasTask(":run")
    if (hasRootRunTask) {
        allTasks.forEach { task ->
            task.enabled = task.name != "run"
        }
    }
}
