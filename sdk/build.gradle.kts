sourceSets {
    main.get().java.setSrcDirs(listOf("framework", "server-api", "player").map { "src/main/$it" })
    named("test") {
        kotlin.srcDir(file("../helpers/test-config/src/main/kotlin"))
    }
}

dependencies {
    api(kotlin("stdlib"))
    api("com.thoughtworks.xstream", "xstream", "1.4.17") // New security config, then 1.4.20
    api("jargs", "jargs", "1.0")
    api("org.slf4j", "slf4j-api", "2.0.9")
    
    implementation("org.hamcrest", "hamcrest-core", "2.2")
    implementation("net.sf.kxml", "kxml2", "2.3.0")
    
    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    // TODO update kotest with Kotlin: https://mvnrepository.com/artifact/io.kotest/kotest-runner-junit5-jvm
}
