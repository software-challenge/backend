package sc.gradle

import org.gradle.api.*
import org.gradle.api.tasks.*
import java.io.File

open class ScriptsTask : DefaultTask() {
    @OutputDirectory
    lateinit var destinationDir: File
    lateinit var fileName: String
    lateinit var content: String

    @TaskAction
    fun createScripts() {
        destinationDir.resolve("$fileName.bat").run {
            writeText(content + " %*")
            setExecutable(true)
        }
        destinationDir.resolve("$fileName.sh").run {
            writeText("#!/bin/sh\n$content \"$@\"")
            setExecutable(true)
        }
    }
}

class PlaceholderPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        // no-op
    }
}