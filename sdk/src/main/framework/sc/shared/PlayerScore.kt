package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.framework.plugins.Constants
import java.math.BigDecimal

@XStreamAlias(value = "score")
data class PlayerScore(
        @XStreamAsAttribute
        val cause: ScoreCause?,
        @XStreamAsAttribute
        val reason: String,
        @XStreamImplicit(itemFieldName = "part")
        val parts: Array<BigDecimal>
) {
    
    constructor(winner: Boolean, reason: String):
            this(ScoreCause.REGULAR, reason, if (winner) Constants.WIN_SCORE else Constants.LOSE_SCORE)
    constructor(cause: ScoreCause?, reason: String, vararg scores: Int):
            this(cause, reason, scores.map { BigDecimal(it) }.toTypedArray())
    
    fun size(): Int = parts.size
    
    fun matches(definition: ScoreDefinition): Boolean =
            size() == definition.size
    
    override fun equals(other: Any?): Boolean =
            other is PlayerScore &&
                    cause == other.cause &&
                    reason == other.reason &&
                    parts.contentEquals(other.parts)
    
    override fun hashCode(): Int {
        var result = parts.contentHashCode()
        result = 31 * result + cause.hashCode()
        result = 31 * result + reason.hashCode()
        return result
    }
    
    fun toString(definition: ScoreDefinition): String {
        if(!matches(definition))
            throw IllegalArgumentException("$definition does not match $this")
        return "PlayerScore(cause=$cause, reason='$reason', parts=[${parts.withIndex().joinToString { "${definition[it.index].name}=${it.value}" }}])"
    }
    
    override fun toString(): String = "PlayerScore(cause=$cause, reason='$reason', parts=${parts.contentToString()})"
}