import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

sourceSets.getByName("main").java.srcDir("src")

application {
    mainClassName = "sc.playerYEAR.Starter"
}

repositories {
    jcenter()
    maven("http://dist.wso2.org/maven2")
    maven("https://jitpack.io")
}

dependencies {
    if (properties["offline"] != null) {
        implementation(fileTree("lib"))
    } else {
        implementation("com.github.CAU-Kiel-Tech-Inf.socha", "GAME", "VERSION")
    }
}

tasks.getByName<ShadowJar>("shadowJar") {
    baseName = "GAME_client"
    classifier = ""
    destinationDir = rootDir
}