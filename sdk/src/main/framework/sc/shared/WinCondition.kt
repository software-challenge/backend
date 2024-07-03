package sc.shared

import sc.api.plugins.ITeam

/**
 * Eine Zusammenfassung eines Sieges mit Sieger und Beschreibung.
 *
 * @param winner Farbe des Siegers.
 * @param reason Text, der den Siegesgrund beschreibt.
 */
data class WinCondition(
        /** Farbe des Gewinners.  */
        val winner: ITeam?,
        /** Siegesgrund.  */
        val reason: IWinReason) : Cloneable {

    fun toString(playerName: String?): String =
            reason.getMessage("$playerName ($winner)")

    override fun toString(): String =
            reason.getMessage((if(reason.isRegular) winner else winner?.opponent()).toString())
    
    override fun equals(other: Any?): Boolean =
        other is WinCondition &&
        other.winner == winner &&
        other.reason == reason
}