package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.Team
import sc.framework.plugins.Player


class MississippiPlayer(team: Team): Player(team) {

    /**
     * Farbe des Spielers
     */
    @XStreamAsAttribute
    val color: Team? = null

    /**
     * aktuelle Punktzahl des Spielers abhängig vom Fortschritt auf dem Spielfeld
     * und der Anzahl der eingesammelten Passagiere
     */
    @XStreamAsAttribute
    val points = 0

    /**
     * aktuelle x-Koordinate des Schiffes
     */
    @XStreamAsAttribute
    val x = 0

    /**
     * aktuelle y-Koordinate des Schiffes
     */
    @XStreamAsAttribute
    val y = 0

    /**
     * Richtung, in die das Schiff ausgerichtet ist.
     */
    @XStreamAsAttribute
    var direction: Direction? = null

    /**
     * aktuelle Geschwindigkeit des Schiffes des Spielers
     */
    @XStreamAsAttribute
    var speed = 0

    /**
     * aktuelle Anzahl der Kohleeinheiten des Schiffes des Spielers
     */
    @XStreamAsAttribute
    var coal = 0

    /**
     * Spielsegment, auf dem sich das Schiff des Spielers befindet
     */
    @XStreamAsAttribute
    val tile = 0

    /**
     * Anzahl der vom Spieler eingesammelten Passagiere
     */
    @XStreamAsAttribute
    val passenger = 0

    /**
     * Nur fuer den Server relevant
     */
    @XStreamOmitField
    var movement = 0

    /**
     * Nur fuer den Server relevant
     */
    @XStreamOmitField
    var freeTurns = 0

    /**
     * Nur fuer die Gui relevant
     */
    @XStreamOmitField
    var freeAcc = 0

    /**
     * Gets the Field object associated with the current Tile index.
     *
     * @param board The Board object representing the game board.
     * @return The Field object associated with the current Tile index, or null if it doesn't exist.
     */
    fun getField(board: Board): Field? {
        for (tile in board.tiles!!) {
            if (tile.index == this.tile) {
                return tile.getField(x, y)
            }
        }
        return null
    }
}