package sc.server

import ch.qos.logback.classic.util.ContextInitializer
import java.io.File

fun logbackFromPWD() {
    System.setProperty("file.encoding", "UTF-8")
    var config = System.getProperty(ContextInitializer.CONFIG_FILE_PROPERTY)
    if (config == null) {
        config = System.getProperty("user.dir") + File.separator + "logback.xml"
        System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, config)
    }
    //println("Loading logback config from $config")
}