sourceSets {
    main.get().java.srcDirs("src/framework", "src/server-api")
    test.get().java.srcDir("src/test")
}

dependencies {
    api(kotlin("stdlib"))
    api("com.thoughtworks.xstream", "xstream", "1.4.11.1")
    api("jargs", "jargs", "1.0")
    api("ch.qos.logback", "logback-classic", "1.2.3")
    
    implementation("org.hamcrest", "hamcrest-core", "2.2")
    implementation("net.sf.kxml", "kxml2", "2.3.0")
    implementation("xmlpull", "xmlpull", "1.1.3.1")
    
    testImplementation("junit", "junit", "4.13")
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", "4.0.5")
    testImplementation("io.kotest", "kotest-assertions-core", "4.0.5")
}

tasks{
    test {
        useJUnitPlatform()
    }
}