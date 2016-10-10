package sc.plugin2016;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "field")
public class Field {
	/**
	 * Farbe des Besitzers des Strommastes auf dem Spielfeld, null, falls kein Strommast auf diesem Feld existiert.
	 */
  @XStreamAsAttribute
	private PlayerColor owner;
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
   * Erzeugt ein neues Feld
   * @param type Typ des Feldes
   * @param x X-Koordinate des Feldes
   * @param y Y-Koordinate des Feldes
   */
	public Field(FieldType type, int x, int y) {
		this.setType(type);
		this.owner = null;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return Den Besitzer, falls es keinen gibt, wird null zurueckgegeben
	 */
	public PlayerColor getOwner() {
		return owner;
	}

	/**
	 * @param owner Besitzer
	 */
	public void setOwner(PlayerColor owner) {
		this.owner = owner;
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
			return f.getOwner().equals(this.getOwner()) && f.getType().equals(this.getType()) 
			    && f.getX() == getX() && f.getY() == getY();
		}
		return false;
	}
	
	/**
	 * Erzeugt eine Deepcopy eines Feldes
	 */
	public Field clone() {
		Field clone = new Field(this.getType(), this.getX(), this.getY());
		clone.setOwner(this.getOwner());
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
    return "Field: x = " + getX() + ", y = " + getY() + ", owner = " + this.getOwner() + ", type = " + this.getType();
  }

}
