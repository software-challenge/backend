package sc.player2021

import jargs.gnu.CmdLineParser
import java.io.File
import kotlin.system.exitProcess

/**
 * Dies ist die Starter Datei, die benutzt wird, um Clients mit dem Server zu verbinden.
 * Wichtig ist die `main` Funktion; diese wird bei Start als erstes ausgeführt.
 *
 * @args [args] Eine Liste aller Parameter, mit der das Programm aufgerufen wurde.
 */
fun main(args: Array<String>) {
    val parser = CmdLineParser()

    /**
     * Dies sind die verschiedenen Optionen, die dem Programm von der Kommandozeile aus übergeben werden können.
     * So kann man mit Start des Programms auswählen,
     * auf welchem Server / zu welchem Spiel sich der Client verbinden soll
     */
    val options = mapOf(
            "host" to parser.addStringOption('h', "host"),
            "port" to parser.addIntegerOption('p', "port"),
            "reservation" to parser.addStringOption('r', "reservation")
    )

    /**
     * Es wird geprüft, ob die vorhanden Argumente Sinn machen, also valide Optionen sind.
     * Bei einem Fehler wird eine kleine Hilfe angezeigt.
     */
    try {
        parser.parse(args)
    } catch (e: CmdLineParser.OptionException) {
        showHelp(e.message.orEmpty())
        exitProcess(2)
    }

    /** Die Optionen werden zu sinnvollen Datentypen umgewandelt. */
    val host = parser.getOptionValue(options["host"],"localhost") as String
    val port = parser.getOptionValue(options["port"], "") as Int
    val reservation = parser.getOptionValue(options["reservation"], "") as String

    /** Hier wird der eigentliche Client gestartet, der dann versucht, sich mit dem gegebenen Server zu verbinden. */
    try {
        SimpleClient(host, port, reservation)
    } catch (e: Exception) {
        SimpleClient.logger.error("Beim starten des Clients ist ein Fehler aufgetreten", e)
        e.printStackTrace()
    }
}

/** Eine Hilfsfunktion, die bei falschem Aufruf des Programms anzeigt, wie das Programm zu verwenden ist. */
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

