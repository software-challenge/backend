package sc.plugin2022.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IWinReason

/** Eine Beschreibung, welches Team gewonnen hat und warum. */
@XStreamAlias(value = "winreason")
enum class WinReason(override val message: String): IWinReason {
    /** Beide Teams haben die gleiche Punktzahl. */
    EQUAL_SCORE("Beide Teams haben die gleiche Punktzahl erzielt."),
    /** Ein Team hat eine höhere Punktzahl als das andere. */
    DIFFERING_SCORES("%s hat am meisten Punkte erzielt.");
}
