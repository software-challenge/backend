val game: String by project

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main"))
        resources.setSrcDirs(listOf("src/resources"))
    }
    test {
        java.setSrcDirs(listOf("src/test"))
    }
}

dependencies {
    api(project(":sdk"))
    
    testImplementation(kotlin("script-runtime")) // for the ManualGameTest
}

tasks {
    jar {
        archiveBaseName.set(game)
    }
}
