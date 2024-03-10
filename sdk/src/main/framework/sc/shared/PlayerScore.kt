package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.framework.plugins.Constants
import java.math.BigDecimal

@XStreamAlias(value = "score")
data class PlayerScore(
        @XStreamImplicit(itemFieldName = "part")
        val parts: Array<BigDecimal>
) {
    
    constructor(vararg scores: Int):
            this(scores.map { BigDecimal(it) }.toTypedArray())
    
    fun size(): Int = parts.size
    
    fun matches(definition: ScoreDefinition): Boolean =
            size() == definition.size
    
    override fun equals(other: Any?): Boolean =
            other is PlayerScore &&
                    parts.contentEquals(other.parts)
    
    override fun hashCode(): Int = parts.contentHashCode()
    
    fun toString(definition: ScoreDefinition): String {
        if(!matches(definition))
            throw IllegalArgumentException("$definition does not match $this")
        return "PlayerScore[${parts.withIndex().joinToString { "${definition[it.index].name}=${it.value}" }}]"
    }
    
    override fun toString(): String = "PlayerScore(${parts.contentToString()})"
}