package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "scoreAggregation")
public enum ScoreAggregation
{
	/**
	 * All values from all games should be summed up.
	 */
	SUM,

	/**
	 * All values from all games should be averaged.
	 */
	AVERAGE
}
