package sc.plugin2021.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IWinReason

@XStreamAlias(value = "winreason")
enum class WinReason(override val message: String): IWinReason {
    EQUAL_SCORE("Das Spiel ist beendet.\nBeide Teams haben die gleiche Punktzahl erzielt.") {
        override fun getMessage(playerName: String?) = message
    },
    DIFFERING_SCORES("Das Spiel ist beended.\n%s hat am meisten Punkte erzielt.") {
        override fun getMessage(playerName: String?) =
                String.format(message, playerName.let{"???"})
    };
}