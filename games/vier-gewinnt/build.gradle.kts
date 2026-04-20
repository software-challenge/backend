dependencies {
    api(project(":sdk"))
}

tasks {
    jar {
        archiveBaseName.set("vier-gewinnt")
    }
}
