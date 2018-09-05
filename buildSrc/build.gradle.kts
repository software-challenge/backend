plugins {
    kotlin("jvm") version "1.2.61"
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
}

sourceSets["main"].java.srcDir("src")