package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.framework.PublicCloneable
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Turn
import sc.plugin2024.util.PluginConstants.START_COAL

/**
 * This class represents a Ship in the game.
 *
 * @property points Die aktuellen Punkte des Schiffes.
 * - Jeder eingesammelte Passagier bringt 5 Punkte.
 * - Jedes überwundene Segment bringt 5 Punkte.
 * - Anhand der Position innerhalb eines Segments werden 0 bis 3 Punkte vergeben.
 * - Ein Spieler, der einen ungültigen Zug macht, erhält für das Spiel insgesamt 0 Punkte.
 *
 * @property position Die aktuelle absolute [CubeCoordinates] des Schiffes auf dem Spielfeld.
 *
 * @property direction Die [CubeDirection], in die das Schiff fährt.
 *
 * @property speed Die aktuelle Geschwindigkeit des Schiffes.
 * - Die Geschwindigkeit eines Schiffes wird durch die Beschleunigungsaktionen bestimmt.
 * - Eine [Acceleration] um eine Geschwindigkeitseinheit pro Zug ist frei, jede weitere [Acceleration] kostet eine Kohleeinheit.
 *
 * @property coal The current number of coal units of the ship.
 * - Jedes Schiff startet zu Beginn des Spiels mit 6 Kohleeinheiten.
 * - Durch verschiedene [Action]s kann sich diese Menge reduzieren.
 *
 * @property passengers Die Anzahl der Passagiere, die der Spieler eingesammelt hat.
 * - Passagiere können an [Field.PASSENGER] eingesammelt werden.
 *
 * @property movement Die Bewegungsreichweite des Schiffes.
 * - Die Bewegungsreichweite wird basierend auf der aktuellen Geschwindigkeit des Dampfers berechnet.
 * - Jeder Bewegungspunkt entspricht einer Einheit der Bewegungsreichweite.
 *
 * @property freeTurns This field is relevant only for the server.
 * - Jeder [Turn] um eine [CubeDirection], also um 60°, ist frei.
 * - Jeder weitere [Turn] kostet eine Kohleeinheit.
 * - Wenn ein Schiff abgedrängt wird, erhält dieses eine weitere freie Drehung.
 * - Dies entfällt, wenn man auf eine [Field.SANDBANK] abgedrängt wird.
 *
 * @property freeAcc This field is relevant only for the GUI.
 * - Jede [Acceleration] um 1 ist frei.
 * - Jede weitere kostet eine Kohleeinheit.
 */
@XStreamAlias(value = "ship")
data class Ship(
        var position: CubeCoordinates,
        @XStreamAsAttribute val team: Team,
        @XStreamAsAttribute var points: Int = 0, // TODO don't track points here
        @XStreamAsAttribute var direction: CubeDirection = CubeDirection.RIGHT,
        @XStreamAsAttribute var speed: Int = 1,
        @XStreamAsAttribute var coal: Int = START_COAL,
        @XStreamAsAttribute var passengers: Int = 0,
        @XStreamAsAttribute var freeTurns: Int = 1,
        @XStreamOmitField var movement: Int = speed,
        @XStreamOmitField var freeAcc: Int = 1,
): PublicCloneable<Ship> {
    override fun clone(): Ship =
            this.copy()
    fun readResolve(): Ship {
        freeAcc = 1
        movement = speed
        return this
    }
}