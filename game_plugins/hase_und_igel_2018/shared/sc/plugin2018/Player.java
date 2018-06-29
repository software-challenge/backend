package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.util.Constants;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Ein Spieler aus Hase- und Igel.
 */
@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable {
  
  /** Farbe der Spielfigur */
  @XStreamAsAttribute
  private PlayerColor color;
  
  /** Position auf dem Spielbrett */
  @XStreamAsAttribute
  private int index;
  
  /** Anzahl der Karotten des Spielers */
  @XStreamAsAttribute
  private int carrots;
  
  /** Anzahl der bisher verspeisten Salate */
  @XStreamAsAttribute
  private int salads;
  
  /** verfügbare Hasenkarten */
  private ArrayList<CardType> cards;
  
  /** letzte Aktion, die kein <code>Skip</code> war */
  private Action lastNonSkipAction;
  
  @XStreamOmitField
  private boolean mustPlayCard;
  
  /**
   * Nur für den Server relevant. W
   * Wird innerhalb eines Zuges genutzt, um zu überpüfen, ob eine Karte gespielt werden muss.
   * Muss nach einem Zug immer false sein, sonst war Zug ungültig.
   *
   * @param mustPlayCard zu setzender Wert
   */
  public void setMustPlayCard(boolean mustPlayCard) {
    this.mustPlayCard = mustPlayCard;
  }
  
  /**
   * Nur für den Server relevant.
   * Wird innerhalb eines Zuges genutzt, um zu überpüfen, ob eine Karte gespielt werden muss.
   * Muss nach einem Zug immer false sein, sonst war Zug ungültig.
   *
   * @return true, falls eine Karte gespielt werden muss
   */
  public boolean mustPlayCard() {
    return mustPlayCard;
  }
  
  /** only for XStream */
  protected Player() {
    cards = new ArrayList<>();
  }
  
  protected Player(PlayerColor color) {
    this(color, 0);
  }
  
  protected Player(PlayerColor color, int position) {
    this(color, position, Constants.INITIAL_CARROTS, Constants.SALADS_TO_EAT, cards());
  }
  
  protected Player(PlayerColor color, int position, int carrots, int salads, ArrayList<CardType> cards) {
    this.index = position;
    this.color = color;
    this.carrots = carrots;
    this.salads = salads;
    this.cards = cards;
  }
  
  private static ArrayList<CardType> cards() {
    ArrayList<CardType> cards = new ArrayList<>(4);
    cards.add(CardType.TAKE_OR_DROP_CARROTS);
    cards.add(CardType.EAT_SALAD);
    cards.add(CardType.HURRY_AHEAD);
    cards.add(CardType.FALL_BACK);
    return cards;
  }
  
  /**
   * Überprüft ob Spieler bestimmte Karte noch besitzt
   *
   * @param type Karte
   *
   * @return true, falls Karte noch vorhanden
   */
  public boolean ownsCardOfType(CardType type) {
    return getCards().contains(type);
  }
  
  /**
   * Die Anzahl an Karotten die der Spieler zur Zeit auf der Hand hat.
   *
   * @return Anzahl der Karotten
   */
  public final int getCarrots() {
    return carrots;
  }
  
  protected final void setCarrots(int carrots) {
    this.carrots = carrots;
  }
  
  /**
   * Ändert Karottenanzahl um angegebenen Wert
   *
   * @param amount Wert um den geändert wird
   */
  public final void changeCarrotsBy(int amount) {
    this.carrots = this.carrots + amount;
  }
  
  /**
   * Die Anzahl der Salate, die dieser Spieler noch verspeisen muss.
   *
   * @return Anzahl der übrigen Salate
   */
  public final int getSalads() {
    return salads;
  }
  
  protected final void setSalads(int salads) {
    this.salads = salads;
  }
  
  /**
   * Verringert Salate um eins. Das essen eines Salats ist nicht erlaubt, sollte keiner mehr vorhanden sein.
   */
  protected final void eatSalad() {
    this.salads = this.salads - 1;
  }
  
  /**
   * Gibt die für diesen Spieler verfügbaren Hasenkarten zurück.
   *
   * @return übrige Karten
   */
  public List<CardType> getCards() {
    return cards;
  }
  
  /**
   * Gibt Karten ohne bestimmte Karte zurück.
   *
   * @param type zu entfernende Karte
   *
   * @return Liste der übrigen Karten
   */
  public List<CardType> getCardsWithout(CardType type) {
    List<CardType> res = new ArrayList<>(4);
    for (CardType b : cards) {
      if (!b.equals(type))
        res.add(b);
    }
    return res;
  }
  
  /**
   * Setzt verfügbare Karten des Spielers, wobei die gegebene Collection geklont wird.
   * Wird vom Server beim Ausführen eines Zuges verwendet.
   *
   * @param cards verfügbare Karten
   */
  public void setCards(Collection<CardType> cards) {
    this.cards = new ArrayList<>(cards);
  }
  
  /**
   * Die aktuelle Position der Figur auf dem Spielfeld.
   * Vor dem ersten Zug steht eine Figur immer auf Spielfeld 0.
   *
   * @return Spielfeldpositionsindex
   */
  public final int getFieldIndex() {
    return index;
  }
  
  /**
   * Setzt die Spielfeldposition eines Spielers. Nur für den Server relevant.
   *
   * @param pos neuer Positionsindex eines Spielers
   */
  public final void setFieldIndex(final int pos) {
    index = pos;
  }
  
  /**
   * Die Farbe dieses Spielers auf dem Spielbrett
   *
   * @return Spielerfarbe
   */
  public final PlayerColor getPlayerColor() {
    return color;
  }
  
  /**
   * Nur für den Server relevant. Setzt Spielerfarbe des Spielers.
   *
   * @param playerColor Spielerfarbe
   */
  public void setPlayerColor(PlayerColor playerColor) {
    this.color = playerColor;
  }
  
  /**
   * Gibt letzte Aktion des Spielers zurück. Wird vom Server zum validieren von Zügen genutzt.
   *
   * @return letzte Aktion
   */
  public Action getLastNonSkipAction() {
    return lastNonSkipAction;
  }
  
  /**
   * Setzt letzte Aktion des Spielers. Nur für den Server relevant beim Ausführen von <code>perform</code>
   * Es wird hier nicht überprüft, ob die Aktion Skip ist.
   */
  public void setLastNonSkipAction(Action lastNonSkipAction) {
    this.lastNonSkipAction = lastNonSkipAction;
  }
  
  
  /**
   * Erzeugt eine deepcopy dieses Spielers
   *
   * @return Klon des Spielers
   */
  public Player clone() {
    Player clone = new Player(color, index, carrots, salads, new ArrayList<>(cards));
    clone.mustPlayCard = this.mustPlayCard;
    if (this.lastNonSkipAction != null)
      clone.lastNonSkipAction = this.lastNonSkipAction.clone();
    return clone;
  }
  
  /**
   * Überprüft, ob Spieler im Ziel. Für den Server für das Überprüfen der WinCondition relevant
   *
   * @return true, falls Spieler auf Zielfeld steht, Sekundärkriterien werden nicht geprüft.
   */
  public boolean inGoal() {
    return index == Constants.LAST_FIELD_INDEX;
  }
  
  @Override
  public String toString() {
    StringBuilder res = new StringBuilder();
    res.append(String.format("Player %s (color: %s, index: %s, carrots: %s, salads: %s)\n", getDisplayName(), color, index, carrots, salads));
    for (CardType type : this.cards)
      res.append(type).append('\n');
    res.append("LastAction ").append(lastNonSkipAction);
    return res.toString();
  }
  
}
