package sc.shared;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sc.helpers.CollectionHelper;
import sc.helpers.Function;

public final class PlayerScore {
	private final BigDecimal[] parts;
	private ScoreCause cause;

	public PlayerScore(boolean winner) {
		this(ScoreCause.REGULAR, 1);
	}

	public PlayerScore(ScoreCause cause, Integer... scores) {
		this(cause, CollectionHelper.iterableToColleciton(
				CollectionHelper.intArrayToBigDecimalArray(scores)).toArray(
				new BigDecimal[scores.length]));
	}

	public PlayerScore(ScoreCause cause, BigDecimal... parts) {
		if (parts == null) {
			throw new IllegalArgumentException("scores must not be null");
		}

		this.parts = parts;
		this.cause = cause;
	}

	public int size() {
		return this.parts.length;
	}

	public ScoreCause getCause() {
		return this.cause;
	}

	public String[] toStrings() {
		return CollectionHelper.iterableToColleciton(
				CollectionHelper.map(Arrays.asList(this.parts),
						new Function<BigDecimal, String>() {
							@Override
							public String operate(BigDecimal val) {
								return val.toString();
							}
						})).toArray(new String[this.parts.length]);
	}

	public void setCause(ScoreCause cause) {
		this.cause = cause;
	}

	public void set(int pos, int value) {
		this.parts[pos] = BigDecimal.valueOf(value);
	}
	
	public List<BigDecimal> getValues() {
		return Collections.unmodifiableList(Arrays.asList(this.parts));
	}
}
