package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.framework.plugins.AbstractPlayer;
import sc.plugin2017.util.Constants;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.
 * Beinhaltet auch Informationen zum Punktekonto,
 * Position des Schiffes, sowie Attribute des Schiffes
 */
@XStreamAlias(value = "player")
public class Player extends AbstractPlayer implements Cloneable {

	/**
	 * Farbe des Spielers
	 */
	@XStreamAsAttribute
	private PlayerColor color;

	 /**
   * aktuelle Punktzahl des Spielers abhängig vom Fortschritt auf dem Spielfeld
   * und der Anzahl der eingesammelten Passagiere
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
   * Richtung, in die das Schiff ausgerichtet ist.
   */
  @XStreamAsAttribute
  private Direction direction;

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
   * Spielsegment, auf dem sich das Schiff des Spielers befindet
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
	  super();
		this.color = color;
		this.points = 1;
		setDirection(Direction.RIGHT);
		setSpeed(1);
		this.coal = Constants.START_COAL;
		setTile(0);
		setX(-1);
		setPassenger(0);
		if(color == PlayerColor.RED) {
		  setY(1);
		} else {
		  setY(-1);
		}
		this.movement = 1;
		this.freeTurns = 1;
		this.freeAcc = 1;
	}

  /**
   * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
   * Deserialisierung von Objekten aus XML-Nachrichten.
   */
  public Player() {
    this.points = 0;
  }

  /**
   * Erstellt einen neuen Spieler mit denselben Eigenschaften wie der uebergebene
   * Spieler. Fuer eigene Implementierungen.
   */
  public Player(Player playerToClone) {
    setPlayerColor(playerToClone.getPlayerColor());
    setPoints(playerToClone.getPoints());
    setX(playerToClone.getX());
    setY(playerToClone.getY());
    setDirection(playerToClone.getDirection());
    setSpeed(playerToClone.getSpeed());
    setCoal(playerToClone.getCoal());
    setTile(playerToClone.getTile());
    setPassenger(playerToClone.getPassenger());
    setMovement(playerToClone.getMovement());
    setFreeTurns(playerToClone.getFreeTurns());
    setFreeAcc(playerToClone.getFreeAcc());
		setDisplayName(playerToClone.getDisplayName());
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
		clone.x = this.x;
		clone.y = this.y;
		clone.direction = this.direction;
		clone.speed = this.speed;
		clone.coal = this.coal;
		clone.tile = this.tile;
		clone.passenger = this.passenger;
    clone.movement = this.movement;
		clone.freeTurns = this.freeTurns;
		clone.freeAcc = this.freeAcc;
		clone.setDisplayName(getDisplayName());
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
		return this.color;
	}

	/**
	 * Nur für den Server relevant
	 * @param color
	 */
	protected void setPlayerColor(PlayerColor color) {
	  this.color = color;
	}

	/**
   * Liefert den die Punkte anhand der Position und der Passagieranzahl des Spielers
   *
   * @return Punkte des Spielers
   */
  public int getPoints() {
    return this.points;
  }



  public void setPoints(int points) {
    this.points = points;
  }

  /**
   * Liefert die x-Koordiante des Schiffes des Spielers
   * @return x-Koordiante
   */
  public int getX() {
    return this.x;
  }

  protected void setX(int x) {
    this.x = x;
  }

  /**
   * Liefert die y-Koordiante des Schiffes des Spielers
   * @return y-Koordiante
   */
  public int getY() {
    return this.y;
  }

  protected void setY(int y) {
    this.y = y;
  }

  /**
   * Richtung, in die das Schiff ausgerichtet ist.
   *
   * @return Die Richtung
   */
  public Direction getDirection() {
    return this.direction;
  }

  protected void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * Liefert die Geschwindigkeit des Spielers
   * @return Geschwindigkeit
   */
  public int getSpeed() {
    return this.speed;
  }

  protected void setSpeed(int speed) {
    this.speed = speed;
  }

  /**
   * Liefert die Kohle des Spielers
   * @return Anzahl der Kohleeinheiten
   */
  public int getCoal() {
    return this.coal;
  }

  /**
   * Erhöht die Kohle um angegeben Wert
   * @param coal Kohle die entfernt wird
   */
  protected void setCoal(int coal) {
    this.coal = coal;
  }

  /**
   * Liefert das Spielsegment, auf dem sich das Schiff des Spielers gerade befindet.
   * @return Segmentnummer
   */
  public int getTile() {
    return this.tile;
  }

  protected void setTile(int tile) {
    this.tile = tile;
  }

  /**
   * Liefert die Anzahl der Passagiere, die der Spieler bisher eingesammelt hat
   * @return Anzahl der Passagiere
   */
  public int getPassenger() {
    return this.passenger;
  }

  protected void setPassenger(int passenger) {
    this.passenger = passenger;
  }

  public Field getField(Board board) {
    for (Tile tile : board.getTiles()) {
      if(tile.getIndex() == this.tile) {
        return tile.getField(this.x, this.y);
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
    if(((getField(board).getFieldInDirection(Direction.RIGHT, board) != null &&
        getField(board).getFieldInDirection(Direction.RIGHT, board).getType() == FieldType.PASSENGER3) ||
        (getField(board).getFieldInDirection(Direction.UP_RIGHT, board) != null &&
        getField(board).getFieldInDirection(Direction.UP_RIGHT, board).getType() == FieldType.PASSENGER4) ||
        (getField(board).getFieldInDirection(Direction.UP_LEFT, board) != null &&
        getField(board).getFieldInDirection(Direction.UP_LEFT, board).getType() == FieldType.PASSENGER5) ||
        (getField(board).getFieldInDirection(Direction.LEFT, board) != null &&
        getField(board).getFieldInDirection(Direction.LEFT, board).getType() == FieldType.PASSENGER0) ||
        (getField(board).getFieldInDirection(Direction.DOWN_LEFT, board) != null &&
        getField(board).getFieldInDirection(Direction.DOWN_LEFT, board).getType() == FieldType.PASSENGER1) ||
        (getField(board).getFieldInDirection(Direction.DOWN_RIGHT, board) != null &&
        getField(board).getFieldInDirection(Direction.DOWN_RIGHT, board).getType() == FieldType.PASSENGER2)) && this.passenger < 2) {
      return true;
    }
    return false;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * Sollte für den Client 0 sein.
   * @return Bewegunspunkte
   */
  public int getMovement() {
    return this.movement;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * @param movement Bewegungspunkte
   */
  public void setMovement(int movement) {
    this.movement = movement;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * Sollte für den Client 0 sein.
   *
   * @return Anzahl der freien Drehzüge
   */
  public int getFreeTurns() {
    return this.freeTurns;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   *
   * @param freeTurns Anzahl der freien Drehzüge
   */
  public void setFreeTurns(int freeTurns) {
    this.freeTurns = freeTurns;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * Sollte für den Client 0 sein.
   * @return Anzahl der freien Beschleunigungen
   */
  public int getFreeAcc() {
    return this.freeAcc;
  }

  /**
   * Nur fuer den Server (und die Gui) relevant.
   * @param freeAcc Anzahl der freien Beschleunigungen
   */
  public void setFreeAcc(int freeAcc) {
    this.freeAcc = freeAcc;
  }

  @Override
  public String toString() {
    return this.color + ", Punkte: " + this.points + " , Position: (" + this.x + ", " + this.y + "), speed, coal: " + this.speed + "," + this.coal + ", Tile: " + this.tile;
  }
}
