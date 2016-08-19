package sc.plugin2017;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2017.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.
 * Beeinhaltet auch Informationen zum Punktekonto, 
 * Position des Schiffes, sowie Attribute des Schiffes
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
   * aktuelle x-Koordinate des Schiffes
   */
  @XStreamAsAttribute
  private int x;
  
  /**
   * aktuelle y-Koordinate des Schiffes
   */
  @XStreamAsAttribute
  private int y;
  
  /**
   * Richtung in die das Schiff ausgerichtet ist. 0 entspricht der Ausrichtung bei der Startposition.
   * Andere Richtungen ergeben sich aufsteigend nach dem Uhrzeigersinn
   */
  @XStreamAsAttribute
  private int direction;
  
  /**
   * aktuelle Geschwindigkeit des Schiffes des Spielers
   */
  @XStreamAsAttribute
  private int speed;
  
  /**
   * aktuelle Anzahl der Kohleeinheiten des Schiffes des Spielers
   */
  @XStreamAsAttribute
  private int coal;
  
  /**
   * Spielsegment auf dem sich das Schiff des Spielers befindet
   */
  @XStreamAsAttribute
  private int tile;
  
  /**
   * Anzahl der vom Spieler eingesammelten Passagiere
   */
  @XStreamAsAttribute
  private int passenger;
  
  /**
   * Nur fuer den Server relevant
   */
  @XStreamOmitField
  private int movement;
  
  /**
   * Nur fuer den Server relevant
   */
  @XStreamOmitField
  private int freeTurns;

  /**
   * Nur fuer die Gui relevant
   */
  @XStreamOmitField
  private int freeAcc;
  
	/**
	 * Erstellt einen Spieler mit gegebener Spielerfarbe.
	 * 
	 * @param color
	 *            Spielerfarbe
	 */
	public Player(final PlayerColor color) {
		this.color = color;
		points = 1;
		setDirection(0);
		setSpeed(1);
		coal = Constants.START_COAL;
		setTile(0);
		setX(-1);
		setPassenger(0);
		if(color == PlayerColor.RED) {
		  setY(1);
		} else {
		  setY(-1);
		}
		movement = 1;
		freeTurns = 1;
		freeAcc = 1;
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
		clone.points = this.points;
		clone.x = x;
		clone.y = y;
		clone.direction = direction;
		clone.speed = speed;
		clone.coal = coal;
		clone.tile = tile;
		clone.passenger = passenger;
    clone.movement = movement;
		clone.freeTurns = freeTurns;
		clone.freeAcc = freeAcc;
		clone.setDisplayName(this.getDisplayName());
		return clone;
	}

	/**
	 * vergleicht zwei Spieler anhand ihrer Spielerfarbe
	 */
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
   * Liefert den die Punkte anhand der Position und der Passagieranzahl des Spielers
   * 
   * @return Punkte des Spielers
   */
  public int getPoints() {
    return points;
  }

  
  
  public void setPoints(int points) {
    this.points = points;
  }

  /**
   * Liefert die x-Koordiante des Schiffes des Spielers
   * @return x-Koordiante
   */
  public int getX() {
    return x;
  }

  protected void setX(int x) {
    this.x = x;
  }

  /**
   * Liefert die y-Koordiante des Schiffes des Spielers
   * @return y-Koordiante
   */
  public int getY() {
    return y;
  }

  protected void setY(int y) {
    this.y = y;
  }

  /**
   * Richtung in die das Schiff ausgerichtet ist. 0 entspricht der Ausrichtung bei der Startposition.
   * Andere Richtungen ergeben sich aufsteigend nach dem Uhrzeigersinn
   *
   * @return Zahl die die Richtung angibt
   */
  public int getDirection() {
    return direction;
  }

  protected void setDirection(int direction) {
    this.direction = direction;
  }

  /**
   * Liefert die Geschwindigkeit des Spielers
   * @return Geschwindigkeit
   */
  public int getSpeed() {
    return speed;
  }

  protected void setSpeed(int speed) {
    this.speed = speed;
  }

  /**
   * Liefert die Kohle des Spielers
   * @return Anzahl der Kohleeinheiten
   */
  public int getCoal() {
    return coal;
  }

  /**
   * Erhöht die Kohle um angegeben Wert
   * @param coal Kohle die entfernt wird
   */
  protected void setCoal(int coal) {
    this.coal = coal;
  }

  /**
   * Liefert das Spielsegment auf dem sich das Schiff des Spielers gerade befindet.
   * @return Segmentnummer
   */
  public int getTile() {
    return tile;
  }

  protected void setTile(int tile) {
    this.tile = tile;
  }

  /**
   * Liefert die Anzahl der Passagiere, die der Spierl bisher eingesammelt hat
   * @return Anzahl der Passagiere
   */
  public int getPassenger() {
    return passenger;
  }

  protected void setPassenger(int passenger) {
    this.passenger = passenger;
  }
  
  public Field getField(Board board) {
    for (Tile tile : board.getTiles()) {
      if(tile.getIndex() == this.tile) {
        return tile.getField(x, y);
      }
    }
    return null;
  }
  
  /**
   * Setzt ein Schiff auf das Spielfeld und entfernt das alte. Diese Methode ist nur für den
   * Server relevant, da hier keine Fehlerüberprüfung durchgeführt wird. Zum
   * Ausführen von Zügen die
   * {@link sc.plugin2017.Move#perform(GameState, Player) perform}-Methode
   * benutzen.
   * 
   * @param x x-Koordinate
   * @param y y-Koordinate
   *          des Feldes, auf das gesetzt wird
   * @param tile Spielsegment auf das gesetzt wird
   */
  protected void put(int x, int y, int tile) {
    this.x = x;
    this.y = y;
    this.tile = tile;
  }

  /**
   * Zeigt an, ob auf dem derzeitigen Feld des Spielers ein Passagier mitgenommen werden kann.
   * @param board Spielfeld
   * @return wahr, falls ein Passagier auf der Position des Spielers eingesammelt werden kann.
   */
  public boolean canPickupPassenger(Board board) {
    if(((getField(board).getFieldInDirection(0, board) != null &&
        getField(board).getFieldInDirection(0, board).getType() == FieldType.PASSENGER3) ||
        (getField(board).getFieldInDirection(1, board) != null &&
        getField(board).getFieldInDirection(1, board).getType() == FieldType.PASSENGER4) ||
        (getField(board).getFieldInDirection(2, board) != null &&
        getField(board).getFieldInDirection(2, board).getType() == FieldType.PASSENGER5) ||
        (getField(board).getFieldInDirection(3, board) != null &&
        getField(board).getFieldInDirection(3, board).getType() == FieldType.PASSENGER0) ||
        (getField(board).getFieldInDirection(4, board) != null &&
        getField(board).getFieldInDirection(4, board).getType() == FieldType.PASSENGER1) ||
        (getField(board).getFieldInDirection(5, board) != null &&
        getField(board).getFieldInDirection(5, board).getType() == FieldType.PASSENGER2)) && passenger < 2) {
      return true;
    }
    return false;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * Sollte für den Client 0 sein.
   * @return
   */
  public int getMovement() {
    return movement;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * @param movement Bewegunspunkte
   */
  public void setMovement(int movement) {
    this.movement = movement;
  }

  public int getFreeTurns() {
    return freeTurns;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * @param freeTurns
   */
  public void setFreeTurns(int freeTurns) {
    this.freeTurns = freeTurns;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * @param freeTurns
   */
  public int getFreeAcc() {
    return freeAcc;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * @param freeTurns
   */
  public void setFreeAcc(int freeAcc) {
    this.freeAcc = freeAcc;
  }
  
  public String toString() {
    return color + ", Punkte: " + points + " , Position: (" + x + ", " + y + "), speed, coal: " + speed + "," + coal + ", Tile: " + tile;
  }
}
