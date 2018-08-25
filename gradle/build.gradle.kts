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

val mainGroup = "_main"
tasks {
    "startServer" {
        dependsOn(":server:run")
        group = mainGroup
    }

    "deploy" {
        dependsOn("clean")
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
            println(gradle.gradleHomeDir)
            println(gradle.gradleUserHomeDir)
            var f = project("server").buildDir.resolve("runnable").resolve("start.bat")
            println("file " + f.absolutePath + " exists: " + f.exists())
            var test = ProcessBuilder("echo \"if exist ./server/build/runnable/start.bat echo file exists else echo file doesn't exist\" >test.bat").start()
            println(test.inputStream.bufferedReader().readLines())
            println(test.errorStream.bufferedReader().readLines())
            f = file("test.bat")
            println("file " + f.absolutePath + " exists: " + f.exists())
            f.writeText("if exist ./server/build/runnable/start.bat echo file exists else echo file doesn't exist")
            println("file " + f.absolutePath + " exists: " + f.exists())
            test = ProcessBuilder("test.bat").start()
            println(test.inputStream.bufferedReader().readLines())
            println(test.errorStream.bufferedReader().readLines())
            val server = ProcessBuilder(if (OperatingSystem.current().isWindows) "cmd /c start ${project("server").buildDir.resolve("runnable").resolve("start.bat").absolutePath}" else "./start.sh").directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(500)
            val client1 = Runtime.getRuntime().exec("java -jar " + buildDir.resolve("deploy").resolve("simpleclient-$game-$version.jar"))
            val client2 = Runtime.getRuntime().exec("java -jar " + buildDir.resolve("deploy").resolve("simpleclient-$game-$version.jar"))
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
                    println("\nserver stdout:")
                    println(server.inputStream.readBytes(server.inputStream.available()).joinToString("") { it.toChar().toString() })
                    println("\nserver stderr:")
                    println(server.errorStream.readBytes(server.errorStream.available()).joinToString("") { it.toChar().toString() })
                    if (server.isAlive) {
                        println("\n$clientName stdout:")
                        println(lines)
                        println("\n$clientName stderr:")
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
    "test" {
        dependsOn("run")
        group = mainGroup
    }
   "build" {
        dependsOn("deploy")
        group = mainGroup
    }
    tasks.replace("run").dependsOn("testDeployed")
    getByName("jar").enabled = false
}

fun Task.dependOnSubprojects() {
    subprojects.forEach {
        it.afterEvaluate {
            dependsOn(it.tasks.findByName(this@dependOnSubprojects.name) ?: return@afterEvaluate)
        }
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    repositories {
        maven("http://dist.wso2.org/maven2")
        jcenter()
    }

    tasks.forEach { if (it.name != "clean") it.mustRunAfter("clean") }
    tasks.withType<Javadoc> {
        val silence = buildDir.resolve("tmp").resolve("silence")
        options.optionFiles!!.add(silence)
        doFirst { silence.writeText("-Xdoclint:none") }
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

// "run" task doesn't work if recursive, see https://stackoverflow.com/q/51903863/6723250
gradle.taskGraph.whenReady {
    val hasRootRunTask = hasTask(":run")
    if (hasRootRunTask) {
        allTasks.forEach { task ->
            task.enabled = task.name != "run"
        }
    }
}
