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
        val builder = StringBuilder("Winner: ").append(winners)
        scores.forEachIndexed { playerIndex, score ->
            builder.appendln().append("Player ").append(playerIndex).append(": ")
            val scoreParts = score.toStrings()
            builder.append(scoreParts.indices.joinToString("; ") { i -> "${definition[i].name}=${scoreParts[i]}" })
        }
        return builder.toString()
    }
    
    val isRegular: Boolean by lazy {
        scores.all { it.cause == ScoreCause.REGULAR }
    }
    
}