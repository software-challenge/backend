package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 
 */
@XStreamAlias(value = "move")
public final class Move implements Cloneable
{
	@XStreamAsAttribute
	private int		n;

	@XStreamAsAttribute
	private MoveTyp	typ;

	@XStreamAsAttribute
	private CardAction	card;

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

	public Move(final MoveTyp t, final CardAction a)
	{
		typ = t;
		card = a;
		n = 0;
	}

	public Move(final MoveTyp t, final CardAction a, final int val)
	{
		typ = t;
		card = a;
		n = val;
	}

	public final int getN()
	{
		return n;
	}

	public final MoveTyp getType()
	{
		return typ;
	}

	public CardAction getCard()
	{
		return card;
	}

	@Override
	public String toString()
	{
		return "type=" + this.getType() + ", card=" + getCard() + ", n="
				+ getN();
	}

	@Override
	protected Move clone() throws CloneNotSupportedException
	{
		return (Move) super.clone();
	}

  public void perform(GameState gameState, Player author) {
    // TODO Auto-generated method stub
    
  }
}
