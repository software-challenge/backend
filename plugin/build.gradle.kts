val game: String by project

sourceSets {
    main.get().java.srcDirs("src/client", "src/server", "src/shared")
    test.get().java.srcDir("src/test")
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
    test {
        useJUnitPlatform()
    }
}
