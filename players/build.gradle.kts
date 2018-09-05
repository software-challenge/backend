import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    application
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

val game = property("game").toString()
val gameName = game.substringBefore("_")

sourceSets {
    getByName("main") {
        java.srcDir("$game/src")
        resources.srcDir("$game/src")
    }
}

application {
    mainClassName = "sc.player2019.Starter"
}

dependencies {
    implementation(project(":plugins"))
}

val deployDir = property("deployDir") as File

tasks {
    "shadowJar"(ShadowJar::class) {
        baseName = "defaultplayer"
        classifier = ""
    }

    tasks.replace("jar").dependsOn("shadowJar")

    val zipDir = buildDir.resolve("zip")

    create<Zip>("deploy") {
        dependsOn("jar", "prepareZip")
        baseName = "simpleclient-$gameName"
        classifier = "src"
        from(zipDir)
        destinationDir = deployDir
        doFirst {
            copy {
                from("build/libs")
                into(deployDir)
                rename("defaultplayer.jar", "simpleclient-$gameName-${rootProject.version}.jar")
            }
        }
    }

    create<Copy>("prepareZip") {
        dependsOn("javadoc", ":sdk:javadoc", ":plugins:javadoc")
        doFirst { zipDir.mkdirs() }
        from(game)
        into(zipDir)
        with(copySpec {
            from(configurations.default)
            into("lib")
        }, copySpec {
            from("build/docs/javadoc")
            into("doc/player-$gameName")
        }, copySpec {
            from("../plugins/build/docs/javadoc")
            into("doc/plugin-$gameName")
        }, copySpec {
            from("../socha-sdk/build/docs/javadoc")
            into("doc/sdk")
        }, copySpec {
            from(rootDir.resolve("gradlew"), rootDir.resolve("gradlew.bat"))
            filter { it.replace(Regex("gradle([/\\\\])wrapper"), "lib$1gradle-wrapper") }
        }, copySpec {
            from(rootDir.resolve("gradle").resolve("wrapper"))
            into("lib/gradle-wrapper")
        })
    }

    "run"(JavaExec::class) {
        args = System.getProperty("args", "").split(" ")
    }
}
