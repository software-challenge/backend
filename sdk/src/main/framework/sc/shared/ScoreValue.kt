package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import java.math.BigDecimal

@XStreamAlias(value = "scoreValue")
data class ScoreValue(val fragment: ScoreFragment, var value: BigDecimal)