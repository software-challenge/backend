val game: String by project

dependencies {
    api(project(":sdk"))
    
    testImplementation(kotlin("script-runtime")) // for the ManualGameTest
}

tasks {
    jar {
        archiveBaseName.set(game)
    }
}
