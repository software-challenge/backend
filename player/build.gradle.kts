import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    application
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

val game = property("game").toString()
val gameName = game.substringBefore("_")
val year = game.substringAfter("_")

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("src")
    }
}

application {
    mainClassName = "sc.player2019.Starter"
}

dependencies {
    implementation(project(":plugin"))
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
        dependsOn("javadoc", ":sdk:javadoc", ":plugin:javadoc")
        into(zipDir)
        with(copySpec {
            from("buildscripts")
            filter {
                it.replace("VERSION", rootProject.version.toString()).replace("GAME", game).replace("YEAR", year)
            }
        }, copySpec {
            from(rootDir.resolve("gradlew"), rootDir.resolve("gradlew.bat"))
            filter { it.replace(Regex("gradle([/\\\\])wrapper"), "lib$1gradle-wrapper") }
        }, copySpec {
            from("src")
            into("src")
        }, copySpec {
            from(configurations.default)
            into("lib")
        }, copySpec {
            from(project(":plugin").buildDir.resolve("docs/javadoc"))
            into("doc/plugin-$gameName")
        }, copySpec {
            from(project(":sdk").buildDir.resolve("docs/javadoc"))
            into("doc/sdk")
        }, copySpec {
            from(rootDir.resolve("gradle").resolve("wrapper"))
            into("lib/gradle-wrapper")
        })

    }

    "run"(JavaExec::class) {
        args = System.getProperty("args", "").split(" ")
    }

}
