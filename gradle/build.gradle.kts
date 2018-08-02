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
        dependsOn("clean", ":players:zip", ":server:zip")
        group = mainGroup
        description = "Zips everything up for release into build/deploy"
    }

    "release" {
        dependsOn("deploy")
        group = mainGroup
        description = "Prepares a new Release by creating a git tag"
        doLast {
            val tagDescription = properties["desc"]?.toString() ?:
                    throw InvalidUserDataException("Die Flag -Pdesc=\"Beschreibung dieser Version\" wird benötigt")
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

    getByName("build").dependsOn("deploy")
    getByName("jar").enabled = false
}

allprojects {
    repositories {
        maven("http://dist.wso2.org/maven2")
        jcenter()
    }
    tasks.forEach { if(it.name != "clean") it.mustRunAfter("clean") }
}

project("sdk") {
    apply(plugin = "java")
    apply(plugin = "kotlin")

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
    apply(plugin = "java")
    apply(plugin = "kotlin")

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