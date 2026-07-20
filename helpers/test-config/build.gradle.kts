dependencies {
    api(project(":sdk"))
    val kotestVersion = "5.9.1"
    api("io.kotest:kotest-assertions-core:$kotestVersion")
    api("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
}
