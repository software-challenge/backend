dependencies {
    api(project(":sdk"))
}

tasks {
    jar {
        archiveBaseName.set("mq")
    }
}
