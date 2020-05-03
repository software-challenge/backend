package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit

@XStreamAlias(value = "scoreDefinition")
class ScoreDefinition(
        @XStreamImplicit(itemFieldName = "fragment")
        private val fragments: MutableList<ScoreFragment> = ArrayList()
): List<ScoreFragment> by fragments {
    
    fun add(string: String) =
            add(ScoreFragment(string))
    
    fun add(fragment: ScoreFragment) =
            fragments.add(fragment)
    
    val isValid: Boolean
        get() = size > 0
    
    override fun toString() =
            "ScoreDefinition[${fragments.joinToString()}]"
    
    override fun equals(other: Any?): Boolean =
            other is ScoreDefinition && fragments == other.fragments
    
    override fun hashCode() =
            fragments.hashCode()
    
}