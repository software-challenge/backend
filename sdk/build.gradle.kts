sourceSets {
    main.get().java.setSrcDirs(listOf("framework", "server-api", "player").map { "src/main/$it" })
    create("testConfig") {
        java.setSrcDirs(listOf("src/test/config"))
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
}

configurations {
    val testConfigApi by getting { extendsFrom(api.get()) }
    val testConfig by creating {
        extendsFrom(testConfigApi)
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    val kt = tasks["compileTestConfigKotlin"]
    add("testConfig", kt.outputs.files.singleFile) {
        builtBy(kt)
    }
}
configurations.archives.get().artifacts.removeIf { it.name == "testConfig" }

dependencies {
    api(kotlin("stdlib"))
    api("com.thoughtworks.xstream", "xstream", "1.4.17")
    api("jargs", "jargs", "1.0")
    api("ch.qos.logback", "logback-classic", "1.2.3")
    
    implementation("org.hamcrest", "hamcrest-core", "2.2")
    implementation("net.sf.kxml", "kxml2", "2.3.0")
    implementation("xmlpull", "xmlpull", "1.1.3.1")
    
    "testConfigApi"("io.kotest", "kotest-assertions-core")
    "testConfigApi"("io.kotest", "kotest-runner-junit5-jvm", "4.6.4")
}
