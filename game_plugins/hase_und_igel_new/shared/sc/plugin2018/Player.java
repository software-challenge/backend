package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import sc.framework.plugins.SimplePlayer;
import sc.shared.PlayerColor;
import sc.plugin2018.util.Constants;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Spieler aus Hase- und Igel.
 * 
 */
// FIXME: make Player a DAO to remove dependencies from ServerGameInterfaces lib
@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable
{
	// Farbe der Spielfigure
  @XStreamAsAttribute
	private PlayerColor color;

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
	private ArrayList<CardType>	cards;

  private Action lastNonSkipAction;

  @XStreamOmitField
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

	protected Player()
	{
		cards = new ArrayList<CardType>();
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

	private void initialize(PlayerColor color, int index)
	{
		this.index = index;
		this.color = color;
		this.carrots = Constants.INITIAL_CARROTS;
		this.salads = Constants.SALADS_TO_EAT;
		this.lastNonSkipAction = new Advance(1);

		cards.add(CardType.TAKE_OR_DROP_CARROTS);
		cards.add(CardType.EAT_SALAD);
		cards.add(CardType.HURRY_AHEAD);
		cards.add(CardType.FALL_BACK);
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean ownsCardOfTyp(CardType type)
	{
		return getCards().contains(type);
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
		this.carrots = Math.max(0, this.carrots + amount); // TODO check why max is used
	}

	/**
	 * Die Anzahl der Salate, die dieser Spieler noch verspeisen muss.
	 * 
	 * @return Anzahl der übrigen Salate
	 */
	public final int getSalads()
	{
		return salads;
	}

  /**
   * Setzt Salate, nur für den Server relevant.
   * @param salads Salate
   */
	protected final void setSalads(int salads)
	{
		this.salads = salads;
	}

  /**
   * Verringert Salate um eins, sollte die Anzahl größer 0 sein. TODO throw exception?
   */
	protected final void eatSalad()
	{
		this.salads = Math.max(0, this.salads - 1);
	}

	/**
	 * Gibt die für diesen Spieler verfügbaren Hasenkarten zurück.
	 * 
	 * @return übrige Karten
	 */
	public List<CardType> getCards()
	{
		if (this.cards == null)
		{
			this.cards = new ArrayList<CardType>();
		}

		return cards;
	}

	public List<CardType> getCardsWithout(CardType a)
	{
		List<CardType> res = new ArrayList<CardType>(4);
		for (CardType b : cards)
		{
			if (!b.equals(a))
				res.add(b);
		}
		return res;
	}

	/**
	 * @param cards Setzt verfügbare Karten es Spielers. Wird vom Server beim ausführen eines Zuges verwendet.
	 */
	public void setCards(List<CardType> cards)
	{
		this.cards = new ArrayList<>(cards);
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
			ret.cards = new ArrayList<CardType>();
			ret.cards.addAll(this.getCards());
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

	public Action getLastNonSkipAction() {
		return lastNonSkipAction;
	}

	public void setLastNonSkipAction(Action lastNonSkipAction) {
		this.lastNonSkipAction = lastNonSkipAction;
	}
}
