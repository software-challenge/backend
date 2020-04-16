package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage

@XStreamAlias(value = "result")
class GameResult(
        val definition: ScoreDefinition,
        @XStreamImplicit(itemFieldName = "score")
        val scores: List<PlayerScore>,
        @XStreamImplicit(itemFieldName = "winner")
        val winners: List<Player>?
): ProtocolMessage {
    
    override fun toString(): String {
        val builder = StringBuilder("Winner: ").append(winners)
        for ((playerIndex, score) in scores.withIndex()) {
            builder.append("\n").append("Player ").append(playerIndex).append(": ")
            val scoreParts = score.toStrings()
            for (i in scoreParts.indices) {
                builder.append(definition[i].name).append("=").append(scoreParts[i])
                builder.append("; ")
            }
        }
        return builder.substring(0, builder.length - 1)
    }
    
    val isRegular: Boolean by lazy {
        scores.all { it.cause == ScoreCause.REGULAR }
    }
    
}