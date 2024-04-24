package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.framework.PublicCloneable
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.util.MQConstants
import sc.plugin2024.util.MQConstants.START_COAL

/**
 * Repräsentiert das Schiff eines Spielers.
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
 * - Die Geschwindigkeit eines Schiffes wird durch die Beschleunigungsaktionen ([Accelerate]) bestimmt.
 * - Eine Änderung der Geschwindikeit um eins pro Zug ist frei, jede weitere kostet eine Kohleeinheit.
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
 * @property freeTurns Erfasst freie Drehungen für diesen Zug.
 * - Eine Drehung um 60° pro Zug ist frei.
 * - Jede weitere Drehung kostet eine Kohleeinheit.
 * - Wenn ein Schiff abgedrängt wird, erhält es eine weitere freie Drehung.
 *
 * @property freeAcc Erfasst freie Beschleunigungen für diesen Zug.
 */
@XStreamAlias(value = "ship")
data class Ship(
    var position: CubeCoordinates,
    @XStreamAsAttribute val team: Team,
    @XStreamAsAttribute var direction: CubeDirection = CubeDirection.RIGHT,
    @XStreamAsAttribute var speed: Int = MQConstants.MIN_SPEED,
    @XStreamAsAttribute var coal: Int = START_COAL,
    @XStreamAsAttribute var passengers: Int = 0,
    @XStreamAsAttribute var freeTurns: Int = 1,
    @XStreamAsAttribute var points: Int = 0, // TODO don't track points here
    @XStreamAsAttribute var stuck: Boolean = false, // TODO consider tracking as -1 points
    @XStreamOmitField var freeAcc: Int = MQConstants.FREE_ACC,
    @XStreamOmitField var movement: Int = speed,
): PublicCloneable<Ship> {
    
    override fun clone(): Ship = this.copy()
    
    fun canTurn() = freeTurns > 0 || coal > 0
    
    
    /** The maximum count of points this speed is able and allowed to accelerate by. */
    val maxAcc: Int
        get() = (coal + freeAcc).coerceAtMost(MQConstants.MAX_SPEED - speed)
    
    /** Adjust speed and movement simultaneously. */
    fun accelerateBy(diff: Int) {
        speed += diff
        movement += diff
    }
    
    fun readResolve(): Ship {
        freeAcc = MQConstants.FREE_ACC
        movement = speed
        return this
    }
}