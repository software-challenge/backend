package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage

/**
 * Das Endergebnis eines Spiels.
 * @property scores die Punktzahlen aller Spieler.
 */
@XStreamAlias(value = "result")
data class GameResult(
        val definition: ScoreDefinition,
        @XStreamImplicit(itemFieldName = "score")
        val scores: List<PlayerScore>,
        val winner: Player?
): ProtocolMessage {
    
    val isRegular: Boolean
        get() = scores.all { it.cause == ScoreCause.REGULAR }
    
    override fun toString() =
            "GameResult(winner=$winner, scores=[${scores.withIndex().joinToString { "Player${it.index + 1}${it.value.toString(definition).removePrefix("PlayerScore")}" }}])"
}