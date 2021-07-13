package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.ITeam
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
        val winner: ITeam?,
): RoomOrchestrationMessage, ObservableRoomMessage {
    
    val isRegular: Boolean
        get() = scores.all { it.value.cause == ScoreCause.REGULAR }
    
    override fun toString() =
            "GameResult(winner=$winner, scores=[${
                scores.entries.joinToString {
                    "${it.key.displayName}${it.value.toString(definition).removePrefix("PlayerScore")}"
                }
            }])"
}