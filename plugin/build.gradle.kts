val game: String by project

sourceSets {
    main.get().java.srcDirs("src/client", "src/server", "src/shared")
    test.get().java.srcDir("src/test")
}

dependencies {
    api(project(":sdk"))
    
    testImplementation(kotlin("script-runtime"))
}

tasks{
    jar {
        archiveBaseName.set(game)
    }
    test {
        useJUnitPlatform()
    }
}
