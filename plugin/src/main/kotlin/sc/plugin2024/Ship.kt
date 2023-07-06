package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import kotlin.math.abs


/**
 * This class represents a Ship in the game.
 *
 * @property points The current points of the ship.
 * @property position The current coordinate of the ship.
 * @property direction The direction the ship is facing.
 * @property speed The current speed of the ship.
 * @property coal The current amount of coal units of the ship.
 * @property tile The tile index where the ship is located.
 * @property passengers The number of passengers collected by the player.
 * @property movement This field is relevant only for the server.
 * @property freeTurns This field is relevant only for the server.
 * @property freeAcc This field is relevant only for the GUI.
 */
class Ship(val team: ITeam, @XStreamAsAttribute val position: Coordinates) {

    /**
     * Aktuelle Punktzahl des Spielers abhängig vom Fortschritt auf dem Spielfeld
     * und der Anzahl der eingesammelten Passagiere
     */
    @XStreamAsAttribute
    val points = 0
    
    /** Richtung, in die das Schiff ausgerichtet ist. */
    @XStreamAsAttribute
    var direction: HexDirection = HexDirection.RIGHT

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
    val passengers = 0

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
    fun getField(board: Board): Field? =
        board.tiles!!.find { it.index == this.tile }?.getField(position!!.x, position!!.y)

    /**
     * Returns the direction after turning by the specified number of times.
     *
     * @param turn the number of times to turn
     * @return the new direction after turning
     */
    fun getTurnedDirection(turn: Int): Vector {
        for (i in Vector.directions.indices) {
            if (Vector.directions[i] == this.direction) {
                return Vector.directions[abs(i + turn) % Vector.directions.size]
            }
        }
        return null!!
    }

    /**
     * Gibt die Anzahl der Drehungen bei einer Drehung von der aktuellen Richtung zu toDir zurück
     * @param toDir Endrichtung
     * @return Anzahl der Drehungen
     */
    fun turnToDir(toDir: Vector) {

    }
}