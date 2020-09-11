val game: String by project

sourceSets {
    main.get().java.srcDirs("src/client", "src/server", "src/shared")
    test.get().java.srcDir("src/test")
}

dependencies {
    api(project(":sdk"))
    
    testImplementation("junit", "junit", "4.13")
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", "4.0.5")
    testImplementation("io.kotest", "kotest-assertions-core", "4.0.5")
    testImplementation(kotlin("script-runtime"))
}

tasks.jar.get().archiveBaseName.set(game)
tasks.test.get().useJUnitPlatform()
