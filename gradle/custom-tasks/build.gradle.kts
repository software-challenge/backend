plugins {
    kotlin("jvm") version "1.6.21"
    `java-gradle-plugin`
}

sourceSets.main.get().java.setSrcDirs(listOf("src"))

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
}

gradlePlugin {
    // we need a placeholder plugin to make the task available, see https://medium.com/p/64ff99344b58#8084
    plugins.register("scripts-task") {
        id = "scripts-task"
        implementationClass = "sc.gradle.PlaceholderPlugin"
    }
}
