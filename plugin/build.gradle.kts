val game: String by project

sourceSets {
    main {
        java.setSrcDirs(listOf("src/shared", "src/client", "src/server"))
        resources.setSrcDirs(listOf("src/resources"))
    }
    test {
        java.setSrcDirs(listOf("src/test"))
    }
}

dependencies {
    api(project(":sdk"))
    
    testImplementation(project(":sdk").dependencyProject.sourceSets.test.get().output)
    testImplementation(kotlin("script-runtime")) // for the ManualGameTest
}

tasks{
    jar {
        archiveBaseName.set(game)
    }
}
