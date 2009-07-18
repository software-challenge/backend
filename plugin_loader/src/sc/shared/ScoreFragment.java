package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "scoreFragment")
public class ScoreFragment {
	
	@XStreamAsAttribute
	private final String name;

	private final ScoreAggregation aggregation;

	private final boolean relevantForRanking;

	public ScoreFragment(String name) {
		this(name, ScoreAggregation.SUM);
	}

	public ScoreFragment(String name, ScoreAggregation aggregation) {
		this(name, aggregation, true);
	}

	public ScoreFragment(String name, ScoreAggregation aggregation,
			boolean relevantForRanking) {
		this.name = name;
		this.aggregation = aggregation;
		this.relevantForRanking = relevantForRanking;
	}

	public String getName() {
		return this.name;
	}

	public ScoreAggregation getAggregation() {
		return aggregation;
	}

	public boolean isRelevantForRanking() {
		return relevantForRanking;
	}
}
