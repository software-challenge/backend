package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import java.math.BigDecimal
import java.math.MathContext

/** How to aggregate the individual score values for final evaluation. */
@XStreamAlias(value = "scoreAggregation")
enum class ScoreAggregation {
    /** Sum the values of all games.  */
    SUM,
    
    /** Average the values of all games.  */
    AVERAGE;
}