package sc.shared;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import sc.helpers.CollectionHelper;
import sc.helpers.Function;

@XStreamAlias(value = "score")
public final class PlayerScore {
	@XStreamImplicit(itemFieldName = "part")
	private final List<BigDecimal> parts;

	@XStreamAsAttribute
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

		this.parts = Arrays.asList(parts);
		this.cause = cause;
	}

	public int size() {
		return this.parts.size();
	}

	public ScoreCause getCause() {
		return this.cause;
	}

	public String[] toStrings() {
		return CollectionHelper.iterableToColleciton(
				CollectionHelper.map(this.parts,
						new Function<BigDecimal, String>() {
							@Override
							public String operate(BigDecimal val) {
								return val.toString();
							}
						})).toArray(new String[this.parts.size()]);
	}

	public void setCause(ScoreCause cause) {
		this.cause = cause;
	}

	public List<BigDecimal> getValues() {
		return Collections.unmodifiableList(this.parts);
	}

	public void setValueAt(int index, BigDecimal v) {
		this.parts.set(index, v);
	}
}
