package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "field")
public class Field implements Comparable<Field>{

  /**
   * Typ des Feldes. Siehe {@link FieldType}
   */
  @XStreamAsAttribute
	private FieldType type;
  /**
   * X-Koordinate dieses Feldes
   */
  @XStreamAsAttribute
	private final int x;
  /**
   * Y-Koordinate dieses Feldes
   */
  @XStreamAsAttribute
	private final int y;


  /**
   * Punkte die ein Feld bringt anhand seiner Position
   */
  @XStreamAsAttribute
  private final int points;

  /**
   * Erzeugt ein neues Feld
   * @param type Typ des Feldes
   * @param x X-Koordinate des Feldes
   * @param y Y-Koordinate des Feldes
   */
	public Field(FieldType type, int x, int y) {
		this.setType(type);
		this.x = x;
		this.y = y;
		points = 0;
	}

	/**
   * Erzeugt ein neues Feld
   * @param type Typ des Feldes
   * @param x X-Koordinate des Feldes
   * @param y Y-Koordinate des Feldes
   * @param points Punkte, die das Feld bringt
   */
  public Field(FieldType type, int x, int y, int points) {
    this.setType(type);
    this.x = x;
    this.y = y;
    this.points = points;
  }

  /**
	 * @return Typ des Feldes
	 */
	public FieldType getType() {
		return type;
	}

	/**
	 * @param type Der zu setzende Feldtyp
	 */
	public void setType(FieldType type) {
		this.type = type;
	}

	/**
	 * Vergleichsmethode fuer ein Feld.
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Field) {
			Field f = (Field) o;
			return f.getType().equals(this.getType())
			    && f.getX() == getX() && f.getY() == getY();
		}
		return false;
	}

	/**
	 * Erzeugt eine Deepcopy eines Feldes
	 */
	@Override
  public Field clone() {
		Field clone = new Field(this.getType(), this.getX(), this.getY(), this.points);
		return clone;
	}

  /**
   * @return X-Koordinate des Feldes
   */
  public int getX() {
    return x;
  }

  /**
   * @return Y-Koordinate des Feldes
   */
  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return "Field: x = " + getX() + ", y = " + getY() + ", type = " + this.getType();
  }

  public int getPoints() {
    return this.points;
  }

  public Field getFieldInDirection(int direction, Board board) {
    return _getFieldInDirection(direction, board, true);
  }

  protected Field alwaysGetFieldInDirection(int direction, Board board) {
    return _getFieldInDirection(direction, board, false);
  }

  private Field _getFieldInDirection(int direction, Board board, boolean onlyVisible) {
    Integer targetX;
    Integer targetY;
    boolean onEvenRow = (y % 2 == 0);
    switch (direction) {
    case 0:
      // field to the right
      targetX = x + 1;
      targetY = y;
      break;
    case 1:
      // field to the upper right
      targetX = onEvenRow ? x + 1 : x;
      targetY = y - 1;
      break;
    case 2:
      // field to the upper left
      targetX = onEvenRow ? x : x - 1;
      targetY = y - 1;
      break;
    case 3:
      // field to the left
      targetX = x - 1;
      targetY = y;
      break;
    case 4:
      // field to the lower left
      targetX = onEvenRow ? x : x - 1;
      targetY = y + 1;
      break;
    case 5:
      // field to the lower right
      targetX = onEvenRow ? x + 1 : x;
      targetY = y + 1;
      break;
    default:
      throw new IllegalArgumentException(String.format("invalid direction: %d", direction));
    }
    if (onlyVisible) {
      return board.getField(targetX, targetY);
    } else {
      return board.alwaysGetField(targetX, targetY);
    }
  }

  /**
   * Ist das Feld passierbar (also entweder Wasser, Sandbank oder Baumstammfeld)?
   * @return Wahr, falls Feld theoretisch passierbar
   */
  public boolean isPassable() {
    return Field.isPassable(this.getType());
  }

  public boolean isBlocked() {
    return !isPassable();
  }

  /**
   * Ist das Feldtyp passierbar (also entweder Wasser, Sandbank oder Baumstammfeld)?
   * @param type Feldtyp
   * @return Wahr, falls Feld theoretisch passierbar
   */
  public static boolean isPassable(FieldType type) {
    return type == FieldType.WATER || type == FieldType.LOG || type == FieldType.SANDBANK || type == FieldType.GOAL;
  }

  /**
   * Ist das Feldtyp ein Passierfeldtyp?
   * @param type Feldtyp
   * @return Wahr, falls Feld Passagierfeld
   */
  public static boolean isPassengerField(FieldType type) {
    return type == FieldType.PASSENGER0 ||
        type == FieldType.PASSENGER1 ||
        type == FieldType.PASSENGER2 ||
        type == FieldType.PASSENGER3 ||
        type == FieldType.PASSENGER4 ||
        type == FieldType.PASSENGER5;
  }

  /**
   * Vergleicht die Lage zweier Felder.
   * @param other Das Feld, mit dem verglichen werden soll.
   * @return -1, falls other weiter rechts unten liegt, 0 wenn die Felder am gleichen Ort liegen, +1 wenn other weiter links oben liegt.
   */
  @Override
  public int compareTo(Field other) {
    // assuming a cartesian coordinate system with lower values in the upper left corner
    if (this.y < other.getY()) {
      // this lies above other
      return -1;
    } else if (this.y > other.getY()) {
      // this lies beneath other
      return 1;
    } else {
      // this lies on same horizontal line as other
      if (this.x < other.getX()) {
        // this lies left to other
        return -1;
      } else if (this.x > other.getX()) {
        // this lies right to other
        return 1;
      } else {
        // this and other are on the same coordinates
        return 0;
      }
    }
  }


}
