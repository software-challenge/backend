import java.util.Scanner

plugins {
    application
}

sourceSets {
    getByName("main").java.srcDir("src")
    getByName("test").java.srcDir("test")
}

application {
    mainClassName = "sc.server.Application"
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8",
            "-XX:+PrintGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-Xloggc:gc.log")
}

dependencies {
    implementation(project(":sdk"))

    testImplementation("junit", "junit", "4.12")
}

val deployDir = property("deployDir") as File

tasks {
    val runnable = file("build/runnable")

    create<Zip>("deploy") {
        dependsOn(":test-client:jar", ":player:jar", "makeRunnable")
        destinationDir = deployDir
        baseName = "software-challenge-server"
        from(project(":test-client").buildDir.resolve("libs"), project(":player").buildDir.resolve("libs"), runnable)
        doFirst {
            runnable.resolve("version").writeText(Scanner(Runtime.getRuntime().exec("git rev-parse HEAD").inputStream).next())
        }
    }

    create<JavaExec>("startProduction") {
        dependsOn("makeRunnable")
        classpath = files(configurations.default, runnable.resolve("software-challenge-server.jar"))
        main = "sc.server.Application"
        workingDir = runnable
        jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dlogback.configurationFile=../../logback-production.xml", "-Djava.security.egd=file:/dev/./urandom", "-XX:MaxGCPauseMillis=100", "-XX:GCPauseIntervalMillis=2050", "-XX:+UseConcMarkSweepGC", "-XX:+CMSParallelRemarkEnabled", "-XX:+UseCMSInitiatingOccupancyOnly", "-XX:CMSInitiatingOccupancyFraction=70", "-XX:+ScavengeBeforeFullGC", "-XX:+CMSScavengeBeforeRemark")
    }

    create<Exec>("dockerImage") {
        dependsOn("makeRunnable")
        val tag = Runtime.getRuntime().exec(arrayOf("git", "rev-parse", "--short", "--verify", "HEAD")).inputStream.reader().readText()
        commandLine("docker", "build", "--no-cache", "-t", "swc_game-server:latest", "-t", "swc_game-server:$tag", "--build-arg", "game_server_dir=$runnable", ".")
    }

    create<Copy>("makeRunnable") {
        dependsOn("jar", "copyPlugin", "createScripts")
        from(configurations.default)
        into(runnable.resolve("lib"))
    }

    create<ScriptsTask>("createScripts") {
        destinationDir = runnable
        fileName = "start"
        content = "java -Dfile.encoding=UTF-8 -Dlogback.configurationFile=logback.xml -jar server.jar"
    }

    create<Copy>("copyPlugin") {
        dependsOn(":plugin:jar")
        from(project(":plugin").buildDir.resolve("libs"))
        into(runnable.resolve("plugins"))
        rename("server.jar", "software-challenge-server.jar")
        doLast {
            copy {
                from("configuration/logback-release.xml", "configuration/server.properties.example")
                into(runnable)
                rename("logback-release.xml", "logback.xml")
                rename("server.properties.example", "server.properties")
            }
        }
    }

    "jar"(Jar::class) {
        destinationDir = runnable
        manifest.attributes["Class-Path"] = configurations.default.joinToString(" ") { "lib/" + it.name }
    }

    "run"(JavaExec::class) {
        dependsOn("copyPlugin")
        workingDir = runnable
        jvmArgs = listOf("-Dlogback.configurationFile=../../configuration/logback.xml")
        args = System.getProperty("args", "").split(" ")
    }

}
