package sc.plugin2010;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author rra
 * @since Jul 4, 2009
 * 
 */
@XStreamAlias(value = "hui:move")
public final class Move implements Cloneable
{
	@XStreamAsAttribute
	private int		n;

	@XStreamAsAttribute
	private MoveTyp	typ;

	@XStreamAsAttribute
	private Action	card;

	public Move(final MoveTyp t)
	{
		typ = t;
		card = null;
		n = 0;
	}

	public Move(final MoveTyp t, int val)
	{
		typ = t;
		card = null;
		n = val;
	}

	public Move(final MoveTyp t, final Action a)
	{
		typ = t;
		card = a;
		n = 0;
	}

	public Move(final MoveTyp t, final Action a, final int val)
	{
		typ = t;
		card = a;
		n = val;
	}

	public final int getN()
	{
		return n;
	}

	public final MoveTyp getTyp()
	{
		return typ;
	}

	public Action getCard()
	{
		return card;
	}

	@Override
	public String toString()
	{
		return "type=" + this.getTyp() + ", card=" + getCard() + ", n="
				+ getN();
	}

	@Override
	protected Move clone() throws CloneNotSupportedException
	{
		return (Move) super.clone();
	}
}
