package sc.plugin2010;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

/**
 * Ein Spieler aus Hase- und Igel.
 * 
 * @author rra
 * @since Jul 4, 2009
 * 
 */
public class Player extends SimplePlayer implements Cloneable
{
	/**
	 * Mögliche Aktionen, die durch das Ausspielen eines Hasenjokers ausgelöst
	 * werden können.
	 */
	public enum Action
	{
		/**
		 * Nehme Karotten auf, oder leg sie ab
		 */
		TAKE_OR_DROP_CARROTS,
		/**
		 * Iß sofort einen Salat
		 */
		EAT_SALAD,
		/**
		 * Falle eine Position zurück
		 */
		FALL_BACK,
		/**
		 * Rücke eine Position vor
		 */
		HURRY_AHEAD
	}

	/**
	 * Alle Spielfiguren aus dem Hase und Igel Original Mit Veränderungen der
	 * CAU
	 */
	public enum FigureColor
	{
		RED, BLUE
	}

	// Farbe der Spielfigure
	private FigureColor		color;

	// Position auf dem Spielbrett
	private int				fieldNumber;

	// Anzahl der Karotten des Spielers
	private int				carrots;

	// Anzahl der bisher verspeisten Salate
	private int				saladsToEat;

	// verfügbare Hasenkarten
	private List<Action>	actions;

	// Die Position des Spielers (1., 2., ..)
	private int				position;

	private List<Move>		history;

	protected void addToHistory(final Move m)
	{
		history.add(m);
	}

	public List<Move> getHistory()
	{
		return history;
	}

	public Move getLastMove()
	{
		Move ret = null;
		if (history.size() > 0)
			ret = history.get(history.size() - 1);
		return ret;
	}

	protected Player(FigureColor color)
	{
		initialize(color, 0);
	}

	protected Player(FigureColor color, int position)
	{
		initialize(color, position);
	}

	private void initialize(FigureColor c, int p)
	{
		fieldNumber = p;
		color = c;
		carrots = 68;
		saladsToEat = 5;

		history = new LinkedList<Move>();
		actions = new ArrayList<Action>(4);
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
		return actions.contains(typ);
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

	public int getPosition()
	{
		return position;
	}

	protected void setPosition(int position)
	{
		this.position = position;
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
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public PlayerScore getScore()
	{
		return new PlayerScore(ScoreCause.REGULAR, this.inGoal() ? 1 : 0,
				getFieldNumber());
	}

	public boolean inGoal()
	{
		return fieldNumber == 64;
	}
}
