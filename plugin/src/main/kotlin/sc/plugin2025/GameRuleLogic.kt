package sc.plugin2025

import kotlin.math.sqrt

object GameRuleLogic {
    /**
     * Berechnet wie viele Karotten für einen Zug der Länge
     * `moveCount` benötigt werden.
     *
     * @param moveCount Anzahl der Felder, um die bewegt wird
     * @return Anzahl der benötigten Karotten
     */
    fun calculateCarrots(moveCount: Int): Int =
        (moveCount * (moveCount + 1)) / 2

    /**
     * Berechnet, wie viele Züge mit `carrots` Karotten möglich sind.
     *
     * @param carrots maximal ausgegebene Karotten
     * @return Felder um die maximal bewegt werden kann
     */
    fun calculateMoveableFields(carrots: Int): Int {
        return when {
            carrots >= 990 -> 44
            carrots < 1 -> 0
            //-0.48 anstelle von -0.5 um Rundungsfehler zu vermeiden
            else -> (sqrt((2.0 * carrots) + 0.25) - 0.48).toInt()
        }
    }
    
}
