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

	// letzte Aktion, die kein <code>Skip</code> war
  private Action lastNonSkipAction;

	@XStreamOmitField
	private boolean			mustPlayCard;

  /**
   * Nur für den Server relevant. Wird innerhalb eines Zuges genutzt, um zu überpüfen, ob eine
   * Karte gespielt werden muss. Muss am nach einem Zug immer false sein, sonst war Zug ungültig.
   * @param mustPlayCard zu setzender Wert
   */
	public void setMustPlayCard(boolean mustPlayCard)
	{
		this.mustPlayCard = mustPlayCard;
	}

  /**
   * Nur für den Server relevant. Wird innerhalb eines Zuges genutzt, um zu überpüfen, ob eine
   * Karte gespielt werden muss. Muss am nach einem Zug immer false sein, sonst war Zug ungültig.
   * @return true, falls eine Karte gespielt werden muss
   */
	public boolean mustPlayCard()
	{
		return mustPlayCard;
	}

	protected Player()
	{
		cards = new ArrayList<>();
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

		cards.add(CardType.TAKE_OR_DROP_CARROTS);
		cards.add(CardType.EAT_SALAD);
		cards.add(CardType.HURRY_AHEAD);
		cards.add(CardType.FALL_BACK);
	}

	/**
	 * Überprüft ob Spieler bestimmte Karte noch besitzt
	 * @param type Karte
	 * @return true, falls Karte noch vorhanden
	 */
	public boolean ownsCardOfTyp(CardType type)
	{
		return getCards().contains(type);
	}

	/**
	 * Die Anzahl an Karotten die der Spieler zur Zeit auf der Hand hat.
	 * 
	 * @return Anzahl der Karotten
	 */
	public final int getCarrots()
	{
		return carrots;
	}

  /**
   * Setzt die Karotten initial
   * @param carrots Anzahl der Karotten
   */
	protected final void setCarrots(int carrots)
	{
		this.carrots = carrots;
	}

  /**
   * Ändert Karottenanzahl um angegebenen Wert
   * @param amount Wert um den geändert wird
   */
	public final void changeCarrotsBy(int amount)
	{
		this.carrots = this.carrots + amount;
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
   * Setzt Salate, nur für den Server relevant. Nur für Tests genutzt.
   * @param salads Salate
   */
	protected final void setSalads(int salads)
	{
		this.salads = salads;
	}

  /**
   * Verringert Salate um eins. Das essen eines Salats ist nicht erlaubt, sollte keiner mehr vorhanden sein.
   */
	protected final void eatSalad()
	{
		this.salads = this.salads - 1;
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
			this.cards = new ArrayList<>();
		}

		return cards;
	}

  /**
   * Gibt Karten ohne bestimmten Typ zurück.
   * @param type Typ der zu entfernenden Karte
   * @return Liste der übrigen Karten
   */
	public List<CardType> getCardsWithout(CardType type)
	{
		List<CardType> res = new ArrayList<>(4);
		for (CardType b : cards)
		{
			if (!b.equals(type))
				res.add(b);
		}
		return res;
	}

	/**
   * Setzt verfügbare Karten es Spielers. Wird vom Server beim ausführen eines Zuges verwendet.
	 * @param cards verfügbare Karten
	 */
	public void setCards(List<CardType> cards)
	{
		this.cards = new ArrayList<>(cards);
	}

	/**
	 * Die aktuelle Position der Figure auf dem Spielfeld. Vor dem ersten Zug
	 * steht eine Figure immer auf Spielfeld 0
	 * 
	 * @return Spielfeldpositionsindex
	 */
	public final int getFieldIndex()
	{
		return index;
	}

  /**
   * Setzt die Spielfeldposition eines Spielers. Nur für den Server relevant.
   * @param pos neuer Positionsindex eines Spielers
   */
	public final void setFieldIndex(final int pos)
	{
		index = pos;
	}

	/**
	 * Die Farbe dieses Spielers auf dem Spielbrett
	 * 
	 * @return Spielerfarbe
	 */
	public final PlayerColor getPlayerColor()
	{
		return color;
	}

  /**
   * Nur für den Server relevant. Setzt Spielerfarbe des Spielers.
   * @param playerColor Spielerfarbe
   */
	public void setPlayerColor(PlayerColor playerColor) {
		this.color = playerColor;
	}

  /**
   * Gibt letzte Aktion des Spielers zurück. Wird vom Server zum validieren von Zügen genutzt.
   * @return letzte Aktion
   */
	public Action getLastNonSkipAction() {
		return lastNonSkipAction;
	}

  /**
   * Setzt letzte Aktion des Spielers. Nur für den Server relevant beim ausführen von <code>perform</code>
   * Es wird hier nicht überprüft, ob die Aktion Skip ist.
   * @param lastNonSkipAction letzte Aktion
   */
	public void setLastNonSkipAction(Action lastNonSkipAction) {
		this.lastNonSkipAction = lastNonSkipAction;
	}


  /**
   * Erzeugt eine deep copy eines Spielers
   * @return Spieler
   */
	public Player clone()
	{
		Player clone = null;
		try
		{
			clone = (Player) super.clone();
			clone.cards = new ArrayList<>();
      clone.cards.addAll(this.cards);
      clone.mustPlayCard = this.mustPlayCard;
			clone.salads = this.salads;
			clone.carrots = this.carrots;
			clone.index = this.index;
			if (this.lastNonSkipAction != null) {
        clone.lastNonSkipAction = this.lastNonSkipAction.clone();
      }
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return clone;
	}

  /**
   * Überprüft, ob Spieler im Ziel. Für den Server für das Überprüfen der WinCondition relevant
   * @return true, falls Spieler auf Zielfeld steht, Sekundärkriterien werden nicht geprüft.
   */
	public boolean inGoal()
	{
		return index == Constants.NUM_FIELDS - 1;
	}

	@Override
	public String toString() {
		String toString =  "Player " + this.getDisplayName() + " (color,index,carrots,salads) " + "("
            + this.color + ","
            + this.index + ","
            + this.carrots + ","
            + this.salads + ")\n";
    for (CardType type: this.cards) {
      toString += type + "\n";
    }
    toString += "LastAction " + this.lastNonSkipAction;
    return toString;
  }
}
