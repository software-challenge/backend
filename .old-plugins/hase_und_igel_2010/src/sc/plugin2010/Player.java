package sc.plugin2010;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Ein Spieler aus Hase- und Igel.
 * 
 * @author rra
 * @since Jul 4, 2009
 * 
 */
// FIXME: make Player a DAO to remove dependencies from ServerGameInterfaces lib
@XStreamAlias(value = "hui:player")
public final class Player extends SimplePlayer implements Cloneable
{
	// Farbe der Spielfigure
	private FigureColor		color;

	// Position auf dem Spielbrett
	private int				fieldNumber;

	// Anzahl der Karotten des Spielers
	private int				carrots;

	// Anzahl der bisher verspeisten Salate
	private int				saladsToEat;

	// verfügbare Hasenkarten
	@XStreamImplicit(itemFieldName = "action")
	private List<Action>	actions;

	@XStreamImplicit(itemFieldName = "move")
	private List<Move>		history;

	private Position		position;

	@XStreamOmitField
	private boolean			mustPlayCard;

	public static final int	SALADS_TO_EAT	= 5;
	public static final int	INITIAL_CARROTS	= 68;

	// FIXME: shouldn't be a property of a DAO that
	// is sent over the network/replay
	public void setMustPlayCard(boolean mustPlayCard)
	{
		this.mustPlayCard = mustPlayCard;
	}

	public boolean mustPlayCard()
	{
		return mustPlayCard;
	}

	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	protected void addToHistory(final Move m)
	{
		getHistory().add(m);
	}

	public List<Move> getHistory()
	{
		if (this.history == null)
		{
			this.history = new LinkedList<Move>();
		}

		return history;
	}

	public Move getLastMove()
	{
		return getLastMove(-1);
	}

	protected Player()
	{
		actions = new LinkedList<Action>();
		history = new LinkedList<Move>();
		// only for XStream
	}

	protected Player(FigureColor color)
	{
		this();
		initialize(color, 0);
	}

	protected Player(FigureColor color, int position)
	{
		this();
		initialize(color, position);
	}

	private void initialize(FigureColor c, int p)
	{
		fieldNumber = p;
		color = c;
		carrots = INITIAL_CARROTS;
		saladsToEat = SALADS_TO_EAT;

		actions.add(Action.TAKE_OR_DROP_CARROTS);
		actions.add(Action.EAT_SALAD);
		actions.add(Action.HURRY_AHEAD);
		actions.add(Action.FALL_BACK);
	}

	/**
	 * @param typ
	 * @return
	 */
	public boolean ownsCardOfTyp(Action typ)
	{
		return getActions().contains(typ);
	}

	/**
	 * Die Anzahl an Karotten die der Spieler zur Zeit auf der Hand hat.
	 * 
	 * @return
	 */
	public final int getCarrotsAvailable()
	{
		return carrots;
	}

	protected final void setCarrotsAvailable(int carrots)
	{
		this.carrots = carrots;
	}

	protected final void changeCarrotsAvailableBy(int amount)
	{
		this.carrots = Math.max(0, this.carrots + amount);
	}

	/**
	 * Die Anzahl der Salate, die dieser Spieler noch verspeisen muss.
	 * 
	 * @return
	 */
	public final int getSaladsToEat()
	{
		return saladsToEat;
	}

	protected final void setSaladsToEat(int saladsToEat)
	{
		this.saladsToEat = saladsToEat;
	}

	protected final void eatSalad()
	{
		this.saladsToEat = Math.max(0, this.saladsToEat - 1);
	}

	/**
	 * Gibt die für diesen Spieler verfügbaren Hasenkarten zurück.
	 * 
	 * @return
	 */
	public List<Action> getActions()
	{
		if (this.actions == null)
		{
			this.actions = new LinkedList<Action>();
		}

		return actions;
	}

	public List<Action> getActionsWithout(Action a)
	{
		List<Action> res = new ArrayList<Action>(4);
		for (Action b : actions)
		{
			if (!b.equals(a))
				res.add(b);
		}
		return res;
	}

	protected void setActions(List<Action> actions)
	{
		this.actions = actions;
	}

	/**
	 * Die aktuelle Position der Figure auf dem Spielfeld. Vor dem ersten Zug
	 * steht eine Figure immer auf Spielfeld 0
	 * 
	 * @return
	 */
	public final int getFieldNumber()
	{
		return fieldNumber;
	}

	protected final void setFieldNumber(final int pos)
	{
		fieldNumber = pos;
	}

	/**
	 * Die Farbe dieses Spielers auf dem Spielbrett
	 * 
	 * @return
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
			ret.actions = new LinkedList<Action>();
			ret.actions.addAll(this.getActions());
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	public PlayerScore getScore(int avg_time)
	{
		return new PlayerScore((violated ? ScoreCause.RULE_VIOLATION : ScoreCause.REGULAR), getPosition().equals(
				Position.FIRST) ? Game.WIN_SCORE : getPosition().equals(
				Position.SECOND) ? Game.LOSE_SCORE : Game.DRAW_SCORE, // Spielergebnis
																		// (WIN/LOSS/TIE)
				getFieldNumber(), // Position auf dem Spielfeld
				getCarrotsAvailable(), // Anzahl verbliebene Karotten
				avg_time); // durchschnittliche Zugzeit in Millisekunden
	}

	public boolean inGoal()
	{
		return fieldNumber == 64;
	}

	public Move getLastNonSkipMove()
	{
		for (int i = getHistory().size() - 1; i >= 0; i--)
		{
			Move lastMove = getHistory().get(i);

			if (lastMove.getTyp() != MoveTyp.SKIP)
			{
				return lastMove;
			}
		}

		return null;
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
