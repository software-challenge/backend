package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import java.math.BigDecimal

@XStreamAlias(value = "scoreDefinition")
class ScoreDefinition(
        @XStreamImplicit(itemFieldName = "fragment")
        private val fragments: Array<ScoreFragment>
): Iterable<ScoreFragment>, RandomAccess {
    
    /** A quick constructor intended for tests. */
    constructor(vararg fragments: String): this(Array<ScoreFragment>(fragments.size) {
        ScoreFragment(fragments[it], WinReason("%s won through ${fragments[it]}"), if(it == 0) ScoreAggregation.SUM else ScoreAggregation.AVERAGE)
    })
    
    val size
        get() = fragments.size
    
    val isValid
        get() = size > 0
    
    fun emptyScore() =
            PlayerScore(Array(size) { BigDecimal.ZERO })
            
    operator fun get(index: Int) =
            fragments[index]
    
    override fun iterator() =
            fragments.iterator()
    
    override fun toString() =
            "ScoreDefinition[${fragments.joinToString()}]"
    
    override fun equals(other: Any?): Boolean =
            other is ScoreDefinition && fragments.contentEquals(other.fragments)
    
    override fun hashCode() =
            fragments.contentHashCode()
    
}