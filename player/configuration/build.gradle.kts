plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

sourceSets.main.get().java.srcDir("src/main")
sourceSets.main.get().resources.srcDir("src/resources")

application {
    mainClass.set("sc.playerYEAR.Starter")
}

repositories {
    jcenter()
    maven("https://maven.wso2.org/nexus/content/groups/wso2-public/")
    maven("https://jitpack.io")
}

dependencies {
    if(gradle.startParameter.isOffline) {
        implementation(fileTree("lib"))
    } else {
        implementation("com.github.software-challenge.backend", "GAME", "VERSION")
    }
}

tasks.shadowJar {
    archiveBaseName.set("GAME_client")
    archiveClassifier.set("")
    destinationDirectory.set(rootDir)
}
