package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

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
		Field clone = new Field(this.getType(), this.getX(), this.getY());
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

  public Field getFieldInDirection(int direction) {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isNeighborOf(Field enemy) {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isPassable() {
    if(type == FieldType.WATER || type == FieldType.LOG || type == FieldType.SANDBAR || type == FieldType.GOAL) {
      return true;
    }
    return false;
  }

}
