rootProject.name = "software-challenge-backend"
rootProject.buildFileName = "gradle/build.gradle.kts"

includeBuild("gradle/custom-tasks")

// TODO rename to name of game rather than plugin from 2027
val plugins = arrayOf("plugin2023", "plugin2024", "plugin2025", "plugin2026")
include("sdk", "server", "player", "test-client", *plugins)
project(":test-client").projectDir = file("helpers/test-client")

val pluginGames = arrayOf("penguins", "mississippi-queen", "hui", "piranhas")
plugins.forEachIndexed { index, plugin ->
    project(":$plugin").projectDir = file("games/${pluginGames[index]}")
}
