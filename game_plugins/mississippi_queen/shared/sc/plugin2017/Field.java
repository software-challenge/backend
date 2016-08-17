package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias(value = "field")
public class Field {

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
  @XStreamOmitField
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
    switch (direction) {
    case 0:
      return board.getField(x+1, y);
    case 1:
      return board.getField((y % 2 == 0) ? x + 1 : x, y - 1);
    case 2:
      return board.getField((y % 2 == 0) ? x : x - 1, y - 1);
    case 3:
      return board.getField(x - 1, y);
    case 4:
      return board.getField((y % 2 == 0) ? x : x - 1, y + 1);
    case 5:
      return board.getField((y % 2 == 0) ? x + 1 : x, y + 1);  
    default:
      break;
    }
    return null;
  }

  /**
   * Ist das Feld passierbar (also entweder Wasser, Sandbank oder Baumstammfeld)?
   * @return Wahr, falls Feld theoretisch passierbar
   */
  public boolean isPassable() {
    if(type == FieldType.WATER || type == FieldType.LOG || type == FieldType.SANDBAR || type == FieldType.GOAL) {
      return true;
    }
    return false;
  }
  
  /**
   * Ist das Feldtyp passierbar (also entweder Wasser, Sandbank oder Baumstammfeld)?
   * @return Wahr, falls Feld theoretisch passierbar
   */
  public static boolean isPassable(FieldType type) {
    return type == FieldType.WATER || type == FieldType.LOG || type == FieldType.SANDBAR || type == FieldType.GOAL;
  }
  
  /**
   * Ist das Feldtyp ein Passierfeldtyp?
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
  

}
