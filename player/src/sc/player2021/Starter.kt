package sc.player2021

import jargs.gnu.CmdLineParser
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = CmdLineParser()
    val options = mapOf(
            "host" to parser.addStringOption('h', "host"),
            "port" to parser.addIntegerOption('p', "port"),
            "reservation" to parser.addStringOption('r', "reservation")
    )
    
    // Parse command line arguments
    try {
        parser.parse(args)
    } catch (e: CmdLineParser.OptionException) {
        showHelp(e.message.orEmpty())
        exitProcess(2)
    }
    
    // Load parameters
    val host = parser.getOptionValue(options["host"],"localhost") as String
    val port = parser.getOptionValue(options["port"], "") as Int
    val reservation = parser.getOptionValue(options["reservation"], "") as String
    
    // Construct a new client
    try {
        SimpleClient(host, port, reservation)
    } catch (e: Exception) {
        SimpleClient.logger.error("Beim starten des Clients ist ein Fehler aufgetreten", e)
        e.printStackTrace()
    }
}

private fun showHelp(errorMsg: String) {
    val jarName = File(SimpleClient::class.java.protectionDomain.codeSource.location.file).name
    println("\n$errorMsg")
    println("""
        Bitte das Programm mit folgenden Parametern (optional) aufrufen:
        java -jar $jarName [{-h, --host} hostname]
                       [{-p, --port} port]
                       [{-r, --reservation} reservation]

        Beispiel:
        java -jar $jarName --host 127.0.0.1 --port 10500 --reservation 1234
    """.trimIndent())
}

