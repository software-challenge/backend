package sc.shared

enum class WinReason(val message: String) {
    BEE_SURROUNDED("Das Spiel ist beendet.\n%s hat die gegnerische Biene umzingelt."),
    BEE_FREE_FIELDS("Das Spiel ist beendet.\n%s hat mehr freie Felder um seine Biene."),
    ROUND_LIMIT_FREE_FIELDS("Das Rundenlimit wurde erreicht.\n%s hat mehr freie Felder um seine Biene."),
    ROUND_LIMIT_EQUAL("Das Rundenlimit wurde erreicht.\nBeide Bienen haben gleich viele freie Felder.");

    fun getMessage(playerName: String) = String.format(message, playerName)
}
