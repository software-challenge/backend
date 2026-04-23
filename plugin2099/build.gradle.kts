val game: String by project

dependencies {
    api(project(":sdk"))
}

tasks {
    jar {
        archiveBaseName.set(game)
    }
}
