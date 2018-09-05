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
        doFirst {
            val server = ProcessBuilder("java", "-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=logback.xml", "-jar", project("server").buildDir.resolve("runnable").resolve("software-challenge-server.jar").absolutePath).directory(project("server").buildDir.resolve("runnable")).start()
            Thread.sleep(300)
            val client1 = Runtime.getRuntime().exec("java -jar " + deployDir.resolve("simpleclient-$gameName-$version.jar"))
            val client2 = Runtime.getRuntime().exec("java -jar " + deployDir.resolve("simpleclient-$gameName-$version.jar"))
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
                    println("server alive: " + server.isAlive)
                    server.inputStream.dump("server stdout")
                    server.errorStream.dump("server stderr")
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

    create<Javadoc>("doc") {
        val projects = arrayOf("player", "plugin", "sdk").map { project(it) }
        source(projects.map { it.sourceSets.getByName("main").allJava })
        classpath = files(projects.map { it.sourceSets.getByName("main").compileClasspath })
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
    getByName("jar").enabled = false
}

// == Cross-project configuration ==

allprojects {
    repositories {
        jcenter()
        maven("http://dist.wso2.org/maven2")
    }
    afterEvaluate {
        tasks {
            forEach { if (it.name != "clean") it.mustRunAfter("clean") }
            withType<Javadoc> {
                val silenceDoc = buildDir.resolve("tmp").resolve("silence")
                doFirst { silenceDoc.apply { parentFile.mkdirs(); writeText("-Xdoclint:none -encoding UTF-8 -charset UTF-8 -docencoding UTF-8") } }
                options.optionFiles!!.add(silenceDoc)
            }
            withType<Test> {
                testLogging { showStandardStreams = System.getProperty("verbose") != null }
            }
            withType<Jar> {
                if (plugins.hasPlugin("application")) {
                    manifest.attributes["Main-Class"] = ((this@allprojects as org.gradle.api.plugins.ExtensionAware).extensions.getByName("application") as org.gradle.api.plugins.JavaApplication).mainClassName
                }
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

    tasks.getByName<Jar>("jar").baseName = "software-challenge-sdk"
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
}

fun Task.dependOnSubprojects() {
    subprojects.forEach {
        it.afterEvaluate {
            dependsOn(tasks.findByName(this@dependOnSubprojects.name) ?: return@afterEvaluate)
        }
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
