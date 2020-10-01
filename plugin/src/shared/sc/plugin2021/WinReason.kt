package sc.plugin2021.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IWinReason

/** Eine Beschreibung, welches Team gewonnen hat und warum. */
@XStreamAlias(value = "winreason")
enum class WinReason(override val message: String): IWinReason {
    /** Beide Teams haben die gleiche Punktzahl. */
    EQUAL_SCORE("Das Spiel ist beendet.\nBeide Teams haben die gleiche Punktzahl erzielt.") {
        override fun getMessage(playerName: String?) = message
    },
    /** Ein Team hat eine höhere Punktzahl als das andere. */
    DIFFERING_SCORES("Das Spiel ist beended.\n%s hat am meisten Punkte erzielt.") {
        override fun getMessage(playerName: String?) =
                String.format(message, playerName.let{"???"})
    };
}