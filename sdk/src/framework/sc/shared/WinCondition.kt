package sc.shared

import sc.api.plugins.ITeam

/**
 * Erzeugt eine neue Condition mit Sieger und Gewinngrund.
 *
 * @param winner Farbe des Siegers.
 * @param reason Text, der Sieg beschreibt.
 */
data class WinCondition(
        /** Farbe des Gewinners.  */
        val winner: ITeam?,
        /** Siegesgrund.  */
        val reason: IWinReason) : Cloneable {

    fun toString(playerName: String?): String =
            reason.getMessage("$playerName ($winner)")

    override fun toString(): String =
            reason.getMessage(winner.toString())
}