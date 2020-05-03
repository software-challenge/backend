package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage

@XStreamAlias(value = "result")
data class GameResult(
        val definition: ScoreDefinition,
        @XStreamImplicit(itemFieldName = "score")
        val scores: List<PlayerScore>,
        @XStreamImplicit(itemFieldName = "winner")
        val winners: List<Player>?
): ProtocolMessage {
    
    val isRegular: Boolean
        get() = scores.all { it.cause == ScoreCause.REGULAR }
    
    override fun toString(): String {
        return "GameResult(winner=$winners, scores=[${(0..scores.lastIndex).joinToString { i -> "Player${i+1}${scores[i].toString(definition).removePrefix("PlayerScore")}" }}])"
    }
    
}