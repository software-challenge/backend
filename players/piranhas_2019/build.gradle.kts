plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "2.0.4"
}

sourceSets.getByName("main").java.srcDir("src")

application {
    mainClassName = "sc.player2019.Starter"
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    if (properties["offline"] != null) {
        implementation(fileTree("lib"))
    } else {
        implementation("com.github.CAU-Kiel-Tech-Inf", "socha", "19.2.0")
    }
}