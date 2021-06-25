package sc.plugin2022.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IWinReason

/** Welches Team gewonnen hat und warum. */
@XStreamAlias(value = "winreason")
enum class WinReason(override val message: String): IWinReason {
    EQUAL_SCORE("Beide Teams sind gleichauf."),
    DIFFERING_SCORES("%s hat mehr Bernsteine erzielt."),
    DIFFERING_POSITIONS("%s hat seine Leichtfiguren weiter vorne."),
}
