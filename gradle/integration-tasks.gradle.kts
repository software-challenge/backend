import org.gradle.kotlin.dsl.support.unzipTo
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Root integration-test workflow.
 *
 * Keeps process-heavy runtime tests out of the main build script.
 */
val bundleDir: File by rootProject.extra
val bundledPlayer: String by rootProject.extra
val integrationReportsDir: File by rootProject.extra
val enableTestClient: Boolean by rootProject.extra
val enableIntegrationTesting: Boolean by rootProject.extra

// TODO create a global constant which can be shared with testclient & co - maybe a resource?
val maxGameLength = 150L // 2m30s

tasks {
    register("testGame") {
        dependsOn(":server:makeRunnable", ":player:bundleShadow")
        group = "verification"
        doFirst {
            val testGameDir = integrationReportsDir.resolve("game")
            testGameDir.deleteRecursively()
            testGameDir.mkdirs()
            val java = "java"
            val port = "13054"
            println("Running on Port: $port")

            val jarTask = project(":server").getTasksByName("jar", false).single() as Jar
            val server = ProcessBuilder(
                java,
                "-Dlogback.configurationFile=${project(":server").projectDir.resolve("configuration/logback-trace.xml")}",
                "-jar", jarTask.archiveFile.get().asFile.absolutePath,
                "--port", port
            )
                .redirectOutput(testGameDir.resolve("server.log"))
                .redirectError(testGameDir.resolve("server-err.log"))
                .directory(jarTask.destinationDirectory.get().asFile)
                .start()

            var i = 0
            while (Files.size(testGameDir.resolve("server.log").toPath()) < 1000 && i++ < 50) {
                Thread.sleep(300)
            }

            val startClient: (Int) -> Process = {
                Thread.sleep(300)
                ProcessBuilder(
                    java,
                    "-jar", bundleDir.resolve(bundledPlayer).absolutePath,
                    "--port", port
                )
                    .redirectOutput(testGameDir.resolve("client$it.log"))
                    .redirectError(testGameDir.resolve("client$it-err.log"))
                    .start()
            }
            startClient(1)
            startClient(2)

            val timeout = AtomicBoolean(false)
            val timeoutThread = Thread {
                try {
                    Thread.sleep(maxGameLength * 1000)
                } catch (_: InterruptedException) {
                    return@Thread
                }
                timeout.set(true)
                println("Killing Server because of timeout!")
                server.destroyForcibly()
            }.apply {
                isDaemon = true
                start()
            }

            try {
                for (clientId in 1..2) {
                    val logFile = testGameDir.resolve("client$clientId.log")
                    var log: String
                    println("Waiting for client $clientId to receive game result")
                    do {
                        if (!server.isAlive) {
                            if (timeout.get()) {
                                throw TimeoutException("$this task exceeded $maxGameLength seconds")
                            }
                            throw Exception("Server terminated unexpectedly!")
                        }
                        Thread.yield()
                        Thread.sleep(100)
                        log = logFile.readText()
                    } while (!log.contains("stop", true))
                    if (!log.contains("Received game result")) {
                        throw Exception("Client $clientId did not receive the game result - check $logFile")
                    }
                }
            } catch (t: Throwable) {
                println("Error in $this - check the logs in $testGameDir")
                throw t
            } finally {
                server.destroy()
            }

            timeoutThread.interrupt()
            println("Successfully played a game using the bundled server & client!")
        }
    }

    register("testTestClient") {
        dependsOn(":server:bundle")
        group = "verification"
        shouldRunAfter("testGame")
        val testClientGames = 3
        doFirst {
            integrationReportsDir.mkdirs()
            val serverDir = integrationReportsDir.resolve("testclient")
            serverDir.deleteRecursively()
            unzipTo(serverDir, bundleDir.resolve("software-challenge-server.zip"))

            val createStartScriptsTask = project(":test-client").getTasksByName("createStartScripts", false).single()
            val launcherContent = createStartScriptsTask.javaClass.getMethod("getContent").invoke(createStartScriptsTask) as String
            val command = launcherContent.split(' ') +
                arrayOf("--start-server", "--tests", testClientGames.toString(), "--port", "13055")
            println("Testing TestClient with $command")

            val testClientOut = serverDir.resolve("testclient.log")
            val testClientErr = serverDir.resolve("testclient-err.log")
            val testClient = ProcessBuilder(command)
                .directory(serverDir)
                .redirectOutput(testClientOut)
                .redirectError(testClientErr)
                .start()
            if (testClient.waitFor(maxGameLength * testClientGames, TimeUnit.SECONDS)) {
                val value = testClient.exitValue()
                if (value == 0) {
                    println("TestClient successfully tested!")
                } else {
                    throw Exception("TestClient exited with exit code $value - check $testClientOut and $testClientErr!")
                }
            } else {
                testClient.destroyForcibly()
                throw Exception("TestClient exceeded timeout of ${maxGameLength * testClientGames} seconds - check $testClientOut and $testClientErr!")
            }
        }
    }

    register("integrationTest") {
        dependsOn("testGame", ":player:playerTest")
        if (enableTestClient) {
            dependsOn("testTestClient")
        }
        group = "verification"
        shouldRunAfter("test")
    }

    named("check") {
        if (enableIntegrationTesting) {
            dependsOn("integrationTest")
        }
    }
}
