import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

val game = property("game").toString()
val gameName = game.substringBefore("_")

java.sourceSets {
    "main" {
        java.srcDir("$game/src")
        resources.srcDir("$game/src")
    }
}

application {
    mainClassName = "sc.player2019.Starter"
}

dependencies {
    compile(project(":sdk"))
    compile(project(":plugins"))
}

tasks {
    "shadowJar"(ShadowJar::class) {
        baseName = "defaultplayer"
        classifier = ""
    }

    tasks.replace("jar").dependsOn("shadowJar")

    val zipDir = buildDir.resolve("zip")

    "deploy"(Zip::class) {
        dependsOn("jar", "prepareZip")
        baseName = "simpleclient-$gameName"
        classifier = "src"
        from(zipDir)
        destinationDir = file("../build/deploy")
        doFirst {
            copy {
                from("build/libs")
                into(rootProject.buildDir.resolve("deploy"))
                rename("defaultplayer.jar", "simpleclient-$gameName-${rootProject.version}.jar")
            }
            copy {
                from("build/zip/doc")
                into(rootProject.buildDir.resolve("deploy").resolve("doc"))
            }
        }
    }

    "prepareZip" {
        dependsOn("javadoc", ":sdk:javadoc", ":plugins:javadoc")
        doFirst {
            zipDir.mkdirs()
            copy {
                from(configurations.compile)
                into(zipDir.resolve("lib"))
            }
            copy {
                from("build/docs/javadoc")
                into(zipDir.resolve("doc").resolve("player-$gameName"))
            }
            copy {
                from("../plugins/build/docs/javadoc")
                into(zipDir.resolve("doc").resolve("plugin-$gameName"))
            }
            copy {
                from("../socha-sdk/build/docs/javadoc")
                into(zipDir.resolve("doc").resolve("sdk"))
            }
            copy {
                from("$game/src")
                into(zipDir.resolve("src"))
            }
        }
    }

    "run"(JavaExec::class) {
        args = System.getProperty("args", "").split(" ")
    }
}
