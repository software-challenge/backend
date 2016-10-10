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
public final class PlayerScore
{
	@XStreamImplicit(itemFieldName = "part")
	private final List<BigDecimal>	parts;

	@XStreamAsAttribute
	private ScoreCause				cause;

	@XStreamAsAttribute
	private String				reason;

	/**
	 * might be needed by XStream
	 */
	public PlayerScore()
	{
		parts = null;
	}

	public PlayerScore(boolean winner, String reason)
	{
		this(ScoreCause.REGULAR, reason, 1);
	}

	public PlayerScore(ScoreCause cause, String reason, Integer... scores)
	{
		this(cause, reason, CollectionHelper.iterableToColleciton(
				CollectionHelper.intArrayToBigDecimalArray(scores)).toArray(
				new BigDecimal[scores.length]));
	}

	public PlayerScore(ScoreCause cause, String reason, BigDecimal... parts)
	{
		if (parts == null)
		{
			throw new IllegalArgumentException("scores must not be null");
		}

		this.parts = Arrays.asList(parts);
		this.cause = cause;
		this.reason = reason;
	}

	public int size()
	{
		return this.parts.size();
	}

	public ScoreCause getCause()
	{
		return this.cause;
	}
	
	public String getReason()
	{
		return this.reason;
	}

	public String[] toStrings()
	{
		return CollectionHelper.iterableToColleciton(
				CollectionHelper.map(parts, new Function<BigDecimal, String>() {
					@Override
					public String operate(BigDecimal val)
					{
						return val.toString();
					}
				})).toArray(new String[parts.size()]);
	}
	
	public String toString() {
		String result = "";
		String[] strings = this.toStrings();
		for (int i = 0; i < strings.length; i++) {
			if (i > 0) result += "; ";
			result += strings[i];
		}
		return result;
	}

	public void setCause(ScoreCause cause)
	{
		this.cause = cause;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<BigDecimal> getValues()
	{
		return Collections.unmodifiableList(parts);
	}

	public void setValueAt(int index, BigDecimal v)
	{
		parts.set(index, v);
	}

	public boolean matches(ScoreDefinition definition)
	{
		return size() == definition.size();
	}
}
