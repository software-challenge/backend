package sc.shared;

public class ScoreFragment {
	public enum Aggregation {
		SUM, AVERAGE
	}

	private final String name;

	private final Aggregation aggregation;

	private final boolean relevantForRanking;

	public ScoreFragment(String name) {
		this(name, Aggregation.SUM);
	}

	public ScoreFragment(String name, Aggregation aggregation) {
		this(name, aggregation, true);
	}

	public ScoreFragment(String name, Aggregation aggregation,
			boolean relevantForRanking) {
		this.name = name;
		this.aggregation = aggregation;
		this.relevantForRanking = relevantForRanking;
	}

	public String getName() {
		return this.name;
	}

	public Aggregation getAggregation() {
		return aggregation;
	}

	public boolean isRelevantForRanking() {
		return relevantForRanking;
	}
}
