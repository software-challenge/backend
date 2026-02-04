plugins {
    java
    application
    id("com.gradleup.shadow") version "9.3.1"
}

sourceSets.main.get().java.srcDir("src/main")
sourceSets.main.get().resources.srcDir("src/resources")

application {
    mainClass.set("sc.player.util.Starter")
}

repositories {
    mavenCentral()
    maven("https://maven.wso2.org/nexus/content/groups/wso2-public/")
    maven("https://jitpack.io")
}

dependencies {
    if(gradle.startParameter.isOffline) {
        implementation(fileTree("lib"))
    } else {
        implementation("com.github.software-challenge.backend", "GAME", "VERSION")
        implementation("ch.qos.logback", "logback-classic", "1.3.15")
    }
}

tasks.shadowJar {
    archiveBaseName.set("GAME_client")
    archiveClassifier.set("")
    destinationDirectory.set(rootDir)
}
