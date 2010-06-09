package sc.plugin_minimal;

import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * A minimal player, it has a color and a move history, nothing more
 * 
 * @author sca
 * 
 */
// FIXME: make Player a DAO to remove dependencies from ServerGameInterfaces lib
@XStreamAlias(value = "minimal:player")
public final class Player extends SimplePlayer implements Cloneable
{
	// Farbe der Spielfigure
	private FigureColor		color;

	@XStreamImplicit(itemFieldName = "move")
	private List<Move>		history;

	/**
	 * Add given move to this player's move history
	 * @param m
	 */
	protected void addToHistory(final Move m)
	{
		getHistory().add(m);
	}

	/**
	 * @return the player's move history
	 */
	public List<Move> getHistory()
	{
		if (this.history == null)
		{
			this.history = new LinkedList<Move>();
		}

		return history;
	}

	/**
	 * 
	 * @return the last move done by this player
	 */
	public Move getLastMove()
	{
		return getLastMove(-1);
	}

	protected Player()
	{
		history = new LinkedList<Move>();
	}

	public Player(FigureColor color) {
		this();
		this.color = color;
	}
	
	/**
	 * 
	 * 
	 * @return Color of this player
	 */
	public final FigureColor getColor()
	{
		return color;
	}

	public Player clone()
	{
		Player ret = null;
		try
		{
			ret = (Player) super.clone();
			ret.history = new LinkedList<Move>();
			ret.history.addAll(this.getHistory());
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 
	 * @param i
	 *            Use -2 to get the move before the last move.
	 * @return
	 */
	public Move getLastMove(int i)
	{
		if (i >= 0)
		{
			throw new IllegalArgumentException("getLastMove requires i < 0");
		}

		if (getHistory() != null && getHistory().size() >= -i)
		{
			return getHistory().get(getHistory().size() + i);
		}

		return null;
	}
}
