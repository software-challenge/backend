package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

@XStreamAlias(value = "scoreFragment")
data class ScoreFragment @JvmOverloads constructor(
        @XStreamAsAttribute
        val name: String,
        val aggregation: ScoreAggregation = ScoreAggregation.SUM,
        val relevantForRanking: Boolean = true) {

    override fun toString(): String =
            "ScoreFragment{name='$name', aggregation=$aggregation, relevantForRanking=$relevantForRanking}"
}