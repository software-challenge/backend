rootProject.name = "software-challenge-backend"
rootProject.buildFileName = "gradle/build.gradle.kts"

includeBuild("gradle/custom-tasks")

include("sdk", "server", "plugin", "plugin2025", "player", "test-client")
project(":test-client").projectDir = file("helpers/test-client")
