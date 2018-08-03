import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

val game = property("game").toString()

java.sourceSets {
    "main" { java.srcDir("$game/src") }
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
        baseName = "simpleclient-$game"
        classifier = rootProject.version.toString()
        destinationDir = file("../build/deploy")
    }

    "jar"(Jar::class) {
        baseName = "defaultplayer"
    }

    "zip"(Zip::class) {
        dependsOn("shadowJar", "javadoc", ":sdk:javadoc", "copySrc")
        baseName = "simpleclient-$game"
        classifier = "src"
        from("build/deploy")
        destinationDir = file("../build/deploy")
    }

    "copySrc" {
        doFirst {
            file("build/deploy").mkdirs()
            copy {
                from(configurations.compile)
                into("build/deploy/lib")
            }
            copy {
                from("build/docs/javadoc")
                into("build/deploy/doc/simple-client")
            }
            copy {
                from("../socha-sdk/build/docs/javadoc")
                into("build/deploy/doc/sdk")
            }
            copy {
                from("$game/src")
                into("build/deploy/src")
            }
        }
    }

    "run"(JavaExec::class) {
        args = System.getProperty("args", "").split(" ")
    }
}