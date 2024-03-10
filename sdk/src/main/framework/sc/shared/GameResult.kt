package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.framework.plugins.Player
import sc.protocol.room.ObservableRoomMessage
import sc.protocol.room.RoomOrchestrationMessage
import sc.util.GameResultConverter

/**
 * Das Endergebnis eines Spiels.
 * @property scores die Punktzahlen aller Spieler.
 */
@XStreamAlias(value = "result")
@XStreamConverter(GameResultConverter::class)
data class GameResult(
        val definition: ScoreDefinition,
        val scores: Map<Player, PlayerScore>,
        val win: WinCondition?,
): RoomOrchestrationMessage, ObservableRoomMessage {
    
    val isRegular: Boolean
        get() = win?.reason?.isRegular ?: true
    
    override fun toString() =
            "GameResult(winner=$win, scores=[${
                scores.entries.joinToString {
                    "${it.key.displayName}${it.value.toString(definition).removePrefix(PlayerScore::class.simpleName.toString())}"
                }
            }])"
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is GameResult) return false
        
        if(definition != other.definition) return false
        if(scores != other.scores) return false
        if(win?.winner != other.win?.winner) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = definition.hashCode()
        result = 31 * result + scores.hashCode()
        result = 31 * result + (win?.winner?.hashCode() ?: 0)
        return result
    }
}