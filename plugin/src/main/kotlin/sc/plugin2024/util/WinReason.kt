package sc.plugin2024.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IWinReason

// TODO koppeln an ScoreDefinition im GamePlugin
/** Welches Team gewonnen hat und warum. */
@XStreamAlias(value = "winreason")
enum class WinReason(override val message: String): IWinReason {
    DIFFERING_SCORES("%s hat mehr Fische gesammelt."),
    DIFFERING_PASSENGERS("%S hat mehr Passagiere befördert."),
    EQUAL_PASSENGERS("Beide Teams haben gleich viele Passagiere befördert."),
}
