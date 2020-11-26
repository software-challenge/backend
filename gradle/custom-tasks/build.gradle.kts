plugins {
    kotlin("jvm") version "1.4.20"
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
}

sourceSets.main.get().java.srcDir("src")

gradlePlugin {
    // we need a placeholder plugin to make the task available, see https://medium.com/p/64ff99344b58#8084
    plugins.register("scripts-task") {
        id = "scripts-task"
        implementationClass = "sc.gradle.PlaceholderPlugin"
    }
}
