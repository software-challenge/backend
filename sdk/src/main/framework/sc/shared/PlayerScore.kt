package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
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
    
    // TODO use constants for WIN and LOSE score
    constructor(winner: Boolean, reason: String):
            this(ScoreCause.REGULAR, reason, if (winner) 2 else 0)
    constructor(cause: ScoreCause?, reason: String, vararg scores: Int):
            this(cause, reason, scores.map { BigDecimal(it) }.toTypedArray())
    
    fun size(): Int = parts.size
    
    val values: List<BigDecimal>
        get() = parts.asList()
    
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