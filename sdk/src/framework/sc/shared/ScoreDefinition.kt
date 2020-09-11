package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit

@XStreamAlias(value = "scoreDefinition")
class ScoreDefinition(
        @XStreamImplicit(itemFieldName = "fragment")
        private val fragments: Array<ScoreFragment>
): Iterable<ScoreFragment>, RandomAccess {
    
    constructor(vararg fragments: String): this(fragments.map { ScoreFragment(it) }.toTypedArray())
    
    val size
        get() = fragments.size
    
    val isValid
        get() = size > 0
    
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