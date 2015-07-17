package sc.plugin2016;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.
 * Beeinhaltet auch Informationen zum Punktekonto
 */
@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable {

	/** 
	 * Spielerfarbe des Spielers
	 */
	@XStreamAsAttribute
	private PlayerColor color;
	
	 /**
   * aktuelle laenge der laengsten Leitung des Spielers
   */
  @XStreamAsAttribute
  private int points;

	/**
	 * Erstellt einen Spieler mit gegebener Spielerfarbe.
	 * 
	 * @param color
	 *            Spielerfarbe
	 */
	public Player(final PlayerColor color) {
		this.color = color;
		points = 0;
	}

  /**
   * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
   * Deserialisierung von Objekten aus XML-Nachrichten.
   */
  public Player() {
    points = 0;
  }

	/**
	 * erzeugt eine Deepcopy dieses Objekts
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException falls klonen fehlschlaegt
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Player clone = new Player(this.color);
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && ((Player) obj).color == this.color;
	}

	/**
	 * liefert die Spielerfarbe dieses Spielers
	 * 
	 * @return Spielerfarbe
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}

	
	/**
   * Liefert den die laengste Leitung des Spielers
   * 
   * @return Punkte des Spielers
   */
  public int getPoints() {
    return points;
  }

  
  
  protected void setPoints(int points) {
    this.points = points;
  }
}
