package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import java.math.BigDecimal
import java.math.MathContext

@XStreamAlias(value = "scoreAggregation")
enum class ScoreAggregation {
    /** All values from all games should be summed up.  */
    SUM,
    
    /** All values from all games should be averaged.  */
    AVERAGE;
}