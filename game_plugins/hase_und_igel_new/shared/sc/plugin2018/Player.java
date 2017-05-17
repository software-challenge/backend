package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.util.Constants;
import sc.shared.PlayerColor;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein Spieler aus Hase- und Igel.
 * 
 */
// FIXME: make Player a DAO to remove dependencies from ServerGameInterfaces lib
@XStreamAlias(value = "player")
public final class Player extends SimplePlayer implements Cloneable
{
	// Farbe der Spielfigure
  @XStreamAsAttribute
	private PlayerColor		color;

	// Position auf dem Spielbrett
  @XStreamAsAttribute
	private int				index;

	// Anzahl der Karotten des Spielers
  @XStreamAsAttribute
	private int				carrots;

	// Anzahl der bisher verspeisten Salate
  @XStreamAsAttribute
	private int				salads;

	// verfügbare Hasenkarten
	@XStreamImplicit(itemFieldName = "cards")
	private List<CardAction>	cards;

	@XStreamImplicit(itemFieldName = "move") // XXX is the history needed?
	private List<Move>		history;

	private Position		position;

	@XStreamOmitField
	private boolean			mustPlayCard;

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

	public void addToHistory(final Move m)
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
		cards = new LinkedList<CardAction>();
		history = new LinkedList<Move>();
		// only for XStream
	}

	protected Player(PlayerColor color)
	{
		this();
		initialize(color, 0);
	}

	protected Player(PlayerColor color, int position)
	{
		this();
		initialize(color, position);
	}

	private void initialize(PlayerColor c, int p)
	{
		index = p;
		color = c;
		carrots = Constants.INITIAL_CARROTS;
		salads = Constants.SALADS_TO_EAT;

		cards.add(CardAction.TAKE_OR_DROP_CARROTS);
		cards.add(CardAction.EAT_SALAD);
		cards.add(CardAction.HURRY_AHEAD);
		cards.add(CardAction.FALL_BACK);
	}

	/**
	 * @param typ
	 * @return
	 */
	public boolean ownsCardOfTyp(CardAction typ)
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

	public final void changeCarrotsAvailableBy(int amount)
	{
		this.carrots = Math.max(0, this.carrots + amount);
	}

	/**
	 * Die Anzahl der Salate, die dieser Spieler noch verspeisen muss.
	 * 
	 * @return
	 */
	public final int getSalads()
	{
		return salads;
	}

	protected final void setSalads(int salads)
	{
		this.salads = salads;
	}

	protected final void eatSalad()
	{
		this.salads = Math.max(0, this.salads - 1);
	}

	/**
	 * Gibt die für diesen Spieler verfügbaren Hasenkarten zurück.
	 * 
	 * @return
	 */
	public List<CardAction> getActions()
	{
		if (this.cards == null)
		{
			this.cards = new LinkedList<CardAction>();
		}

		return cards;
	}

	public List<CardAction> getActionsWithout(CardAction a)
	{
		List<CardAction> res = new ArrayList<CardAction>(4);
		for (CardAction b : cards)
		{
			if (!b.equals(a))
				res.add(b);
		}
		return res;
	}

	public void setActions(List<CardAction> actions)
	{
		this.cards = actions;
	}

	/**
	 * Die aktuelle Position der Figure auf dem Spielfeld. Vor dem ersten Zug
	 * steht eine Figure immer auf Spielfeld 0
	 * 
	 * @return
	 */
	public final int getFieldIndex()
	{
		return index;
	}

	public final void setFieldNumber(final int pos)
	{
		index = pos;
	}

	/**
	 * Die Farbe dieses Spielers auf dem Spielbrett
	 * 
	 * @return
	 */
	public final PlayerColor getPlayerColor()
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
			ret.cards = new LinkedList<CardAction>();
			ret.cards.addAll(this.getActions());
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	public PlayerScore getScore()
	{
		return new PlayerScore((violated ? ScoreCause.RULE_VIOLATION : ScoreCause.REGULAR), "test" /*TODO add reason*/,  getPosition().equals(
				Position.FIRST) ? Constants.WIN_SCORE : getPosition().equals(
				Position.SECOND) ? Constants.LOSE_SCORE : Constants.DRAW_SCORE, // Spielergebnis
																		// (WIN/LOSS/TIE)
				getFieldIndex(), // Position auf dem Spielfeld
				getCarrotsAvailable() // Anzahl verbliebene Karotten
				);
	}

	public boolean inGoal()
	{
		return index == 64;
	}

	public Move getLastNonSkipMove()
	{
		for (int i = getHistory().size() - 1; i >= 0; i--)
		{
			Move lastMove = getHistory().get(i);

			if (lastMove.getType() != MoveTyp.SKIP)
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
