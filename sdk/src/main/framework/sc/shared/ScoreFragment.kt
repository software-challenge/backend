package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField

@XStreamAlias(value = "scoreFragment")
data class ScoreFragment @JvmOverloads constructor(
        @XStreamAsAttribute
        val name: String,
        @XStreamOmitField
        /** Only present on the server, for generating the [GameResult]. */
        val explanation: IWinReason,
        val aggregation: ScoreAggregation = ScoreAggregation.AVERAGE,
        @XStreamOmitField
        /** Whether lower points are better. */
        val invert: Boolean = false,
        val relevantForRanking: Boolean = true
) {

    override fun toString(): String =
            "ScoreFragment{name='$name', aggregation=$aggregation, relevantForRanking=$relevantForRanking}"
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is ScoreFragment) return false
        
        if(name != other.name) return false
        if(aggregation != other.aggregation) return false
        if(relevantForRanking != other.relevantForRanking) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + aggregation.hashCode()
        result = 31 * result + relevantForRanking.hashCode()
        return result
    }
}