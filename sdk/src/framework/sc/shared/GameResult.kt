package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.framework.plugins.Player
import sc.protocol.room.ObservableRoomMessage
import sc.protocol.room.RoomOrchestrationMessage

/**
 * Das Endergebnis eines Spiels.
 * @property scores die Punktzahlen aller Spieler.
 * @property winners eine Liste an Gewinnern, sofern es welche gibt.
 */
@XStreamAlias(value = "result")
data class GameResult(
        val definition: ScoreDefinition,
        @XStreamImplicit(itemFieldName = "score")
        val scores: List<PlayerScore>,
        @XStreamImplicit(itemFieldName = "winner")
        val winners: List<Player>?
): RoomOrchestrationMessage, ObservableRoomMessage {
    
    val isRegular: Boolean
        get() = scores.all { it.cause == ScoreCause.REGULAR }
    
    override fun toString() =
            "GameResult(winner=$winners, scores=[${scores.withIndex().joinToString { "Player${it.index + 1}${it.value.toString(definition).removePrefix("PlayerScore")}" }}])"
    
    override fun equals(other: Any?) =
            other is GameResult &&
                    definition == other.definition &&
                    scores == other.scores &&
                    (winners == other.winners ||
                            (winners.isNullOrEmpty() && other.winners.isNullOrEmpty()))
    
    override fun hashCode(): Int {
        var result = definition.hashCode()
        result = 31 * result + scores.hashCode()
        result = 31 * result + (winners ?: emptyList()).hashCode()
        return result
    }
    
}