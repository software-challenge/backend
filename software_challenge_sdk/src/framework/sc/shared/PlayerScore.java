package sc.shared;

import java.math.BigDecimal;
import java.util.*;

import sc.helpers.CollectionHelper;
import sc.helpers.Function;

import com.thoughtworks.xstream.annotations.*;

@XStreamAlias(value = "score")
public final class PlayerScore
{
	@XStreamImplicit(itemFieldName = "part")
	private final List<BigDecimal>	parts;

	@XStreamAsAttribute
	private ScoreCause				cause;

	/**
	 * might be needed by XStream
	 */
	public PlayerScore()
	{
		parts = null;
	}

	public PlayerScore(boolean winner)
	{
		this(ScoreCause.REGULAR, 1);
	}

	public PlayerScore(ScoreCause cause, Integer... scores)
	{
		this(cause, CollectionHelper.iterableToColleciton(
				CollectionHelper.intArrayToBigDecimalArray(scores)).toArray(
				new BigDecimal[scores.length]));
	}

	public PlayerScore(ScoreCause cause, BigDecimal... parts)
	{
		if (parts == null)
		{
			throw new IllegalArgumentException("scores must not be null");
		}

		this.parts = Arrays.asList(parts);
		this.cause = cause;
	}

	public int size()
	{
		return parts.size();
	}

	public ScoreCause getCause()
	{
		return cause;
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

	public void setCause(ScoreCause cause)
	{
		this.cause = cause;
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
