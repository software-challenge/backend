package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "scoreAggregation")
public enum ScoreAggregation {
	SUM, AVERAGE
}
