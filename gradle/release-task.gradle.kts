import org.gradle.api.GradleException

/**
 * Release automation task.
 *
 * Handles version bumping in gradle.properties and performs the corresponding git commit/tag/push.
 */
val versionObject: KotlinVersion by rootProject.extra

tasks {
    register("release") {
        dependsOn("clean", "check")
        group = "distribution"
        description = "Prepares a new Release by bumping the version and pushing a commit tagged with the new version"
        doLast {
            var newVersion = project.version.toString()

            fun String.editVersion(version: String, new: Int) =
                if (startsWith("socha.version.$version")) {
                    "socha.version.$version=${new.toString().padStart(2, '0')}"
                } else {
                    this
                }

            val versionLineUpdater: (String) -> String = when {
                project.hasProperty("manual") -> ({ it })
                project.hasProperty("minor") -> ({
                    newVersion = "${versionObject.major}.${versionObject.minor + 1}.0"
                    it.editVersion("minor", versionObject.minor + 1).editVersion("patch", 0)
                })
                project.hasProperty("patch") -> ({
                    newVersion = "${versionObject.major}.${versionObject.minor}.${versionObject.patch + 1}"
                    it.editVersion("patch", versionObject.patch + 1)
                })
                else -> throw InvalidUserDataException(
                    "Gib entweder -Ppatch oder -Pminor an, um die Versionsnummer automatisch zu inkrementieren, oder ändere sie selbst in gradle.properties und gib dann -Pmanual an!"
                )
            }

            val desc = project.properties["m"]?.toString()
                ?: throw InvalidUserDataException("Das Argument -Pm=\"Beschreibung dieser Version\" wird benötigt")

            val propsFile = file("gradle.properties")
            propsFile.writeText(propsFile.readLines().joinToString("\n") { versionLineUpdater(it) })

            println("Version: $newVersion")
            println("Beschreibung: $desc")

            fun runGit(vararg args: String) {
                val process = ProcessBuilder(listOf("git") + args)
                    .inheritIO()
                    .start()
                val exitCode = process.waitFor()
                if (exitCode != 0) {
                    throw GradleException("git ${args.joinToString(" ")} failed with exit code $exitCode")
                }
            }

            runGit("add", "gradle.properties", "CHANGELOG.md")
            runGit("commit", "-m", "release: v$newVersion")
            runGit("tag", newVersion, "-m", desc)
            runGit("push", "--follow-tags")
        }
    }
}
