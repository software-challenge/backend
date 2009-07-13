package sc.shared;

import java.math.BigInteger;
import java.util.Arrays;

import sc.helpers.CollectionHelper;
import sc.helpers.Function;

public final class PlayerScore {
	private final BigInteger[] bigIntegers;
	private ScoreCause cause;

	public PlayerScore(boolean winner) {
		this(ScoreCause.REGULAR, 1);
	}

	public PlayerScore(ScoreCause cause, Integer... scores) {
		this(cause, CollectionHelper.iterableToColleciton(
				CollectionHelper.intArrayToBigIntArray(scores)).toArray(
				new BigInteger[scores.length]));
	}

	public PlayerScore(ScoreCause cause, BigInteger... bigIntegers) {
		if (bigIntegers == null) {
			throw new IllegalArgumentException("scores must not be null");
		}

		this.bigIntegers = bigIntegers;
		this.cause = cause;
	}

	public int size() {
		return this.bigIntegers.length;
	}

	public ScoreCause getCause() {
		return this.cause;
	}

	public String[] toStrings() {
		return CollectionHelper.iterableToColleciton(
				CollectionHelper.map(Arrays.asList(this.bigIntegers),
						new Function<BigInteger, String>() {
							@Override
							public String operate(BigInteger val) {
								return val.toString();
							}
						})).toArray(new String[this.bigIntegers.length]);
	}

	public void setCause(ScoreCause cause) {
		this.cause = cause;
	}

	public void set(int pos, int value) {
		this.bigIntegers[pos] = BigInteger.valueOf(value);
	}

}
