package sc.plugin2020.util

import sc.shared.IWinReason

// Copy of Hive WinReason due to package import issues...
enum class WinReason (override val message: String): IWinReason {
    BEE_SURROUNDED("Das Spiel ist beendet.\n%s hat die gegnerische Biene umzingelt."),
    BEE_FREE_FIELDS("Das Spiel ist beendet.\n%s hat mehr freie Felder um seine Biene."),
    ROUND_LIMIT_FREE_FIELDS("Das Rundenlimit wurde erreicht.\n%s hat mehr freie Felder um seine Biene."),
    ROUND_LIMIT_EQUAL("Das Rundenlimit wurde erreicht.\nBeide Bienen haben gleich viele freie Felder.");
    
    override fun getMessage(playerName: String?): String {
        return String.format(message, playerName)
    }
}
