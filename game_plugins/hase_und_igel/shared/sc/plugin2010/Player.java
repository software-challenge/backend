package sc.plugin2010;

import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;

/**
 * Ein Spieler aus Hase- und Igel.
 * 
 * @author rra
 * @since Jul 4, 2009
 * 
 */
public class Player extends SimplePlayer
{
	/**
	 * Mögliche Aktionen, die durch das Ausspielen einer Hasenkarte ausgelöst
	 * werden können.
	 */
	public enum Action // TODO only 4 cards...
	{
		/**
		 * Einmal aussetzen
		 */
		SUSPENDED,
		/**
		 * Ziehe ein Hasenfeld vor
		 */
		NEXT_RABBIT_FIELD,
		/**
		 * Falle ein Hasenfeld zurück
		 */
		LAST_RABBIT_FIELD,
		/**
		 * Ziehe gleich noch einmal
		 */
		MOVE_AGAIN,
		/**
		 * Der Zug war kostenlos
		 */
		FREE_MOVEMENT,
		/**
		 * Nehme 20 Karotten auf (Veränderung CAU)
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
	private int				position;

	// Anzahl der Karotten des Spielers
	private int				carrots;

	// Anzahl der bisher verspeisten Salate
	private int				saladsToEat;

	// verfügbare Hasenkarten
	private List<Action>	actions;

	// Muss der Spieler in der kommenden Runde aussetzen?
	private boolean			suspended;

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
		position = p;
		color = c;
		carrots = 68;
		saladsToEat = 5;
		suspended = false;

		actions = new LinkedList<Action>();
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

	/**
	 * Eine Hasenkarte kann den Spieler eine Runde aussetzen lassen.
	 * <code>TRUE</code>, falls der Spieler in der kommenden Runde aussetzen
	 * muss.
	 * 
	 * @return
	 */
	public boolean isSuspended()
	{
		return suspended;
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
	public final int getPosition() // TODO not position, it's fieldnumber
	{
		return position;
	}

	// TODO getPosition (if the player is first or second)

	protected final void setPosition(final int pos)
	{
		position = pos;
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
}
