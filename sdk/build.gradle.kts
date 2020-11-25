sourceSets {
    main.get().java.srcDirs("src/framework", "src/server-api")
    test.get().java.srcDir("src/test")
}

dependencies {
    api(kotlin("stdlib"))
    api("com.thoughtworks.xstream", "xstream", "1.4.13")
    api("jargs", "jargs", "1.0")
    api("ch.qos.logback", "logback-classic", "1.2.3")
    
    implementation("org.hamcrest", "hamcrest-core", "2.2")
    implementation("net.sf.kxml", "kxml2", "2.3.0")
    implementation("xmlpull", "xmlpull", "1.1.3.1")
}
