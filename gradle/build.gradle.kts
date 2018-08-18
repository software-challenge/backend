import org.gradle.internal.os.OperatingSystem
import java.util.zip.ZipInputStream

plugins {
    java
    kotlin("jvm") version "1.2.60"
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
        group = mainGroup
        description = "Zips everything up for release into build/deploy"
    }

    "release" {
        dependsOn("deploy")
        group = mainGroup
        description = "Prepares a new Release by creating a git tag"
        doLast {
            val tagDescription = properties["desc"]?.toString()
                    ?: throw InvalidUserDataException("Die Flag -Pdesc=\"Beschreibung dieser Version\" wird benötigt")
            println("Beschreibung: $tagDescription")
            exec { commandLine("git", "tag", version, "-m", tagDescription) }
            exec { commandLine("git", "push", "--tags") }
            println("""===================================================
    Fertig! Jetzt noch folgende Schritte ausfuehren:
     - auf der Website (http://www.software-challenge.de/wp-admin) unter Medien die Dateien ersetzen
     - unter Seiten die Downloadseite aktualisieren (neue Version in Versionshistorie eintragen)

    Dann auf der Wettkampfseite (http://contest.software-challenge.de) was unter Aktuelles schreiben und auf die Downloadseite verlinken:

    Eine neue Version der Software ist verfügbar! $description
    Dafür gibt es einen neuen Server und Simpleclient im [Download-Bereich der Website][1].

    [1]: http://www.software-challenge.de/downloads/

    Dann noch etwas im Discord-Chat in #news schreiben:
    Good news @everyone! Neue Version der Software mit Fehlerbehebungen! http://www.software-challenge.de/downloads/""")
        }
    }

    "testDeployed" {
        dependsOn("deploy")
        doFirst {
            val server = ProcessBuilder("./start." + if (OperatingSystem.current().isWindows) "bat" else "sh").directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(200)
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

    tasks.replace("run").run {
        dependsOn("testDeployed")
    }

    "clean" {
        subprojects.forEach {
            it.afterEvaluate {
                val cleanTask = it.tasks.findByName("clean")
                if (cleanTask != null)
                    dependsOn(cleanTask)
            }
        }
    }

    getByName("test").dependsOn("testDeployed")
    getByName("build").dependsOn("deploy")
    getByName("jar").enabled = false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")

    repositories {
        maven("http://dist.wso2.org/maven2")
        jcenter()
    }

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
        "test" { java.srcDir("test") }
    }

    dependencies {
        compile(project(":sdk"))
    }

    tasks {
        "jar"(Jar::class) {
            baseName = game
        }
    }
}