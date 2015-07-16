package sc.plugin2016;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "field")
public class Field {
  @XStreamAsAttribute
	private PlayerColor owner;
  @XStreamAsAttribute
	private FieldType type;
  @XStreamAsAttribute
	private final int x;
  @XStreamAsAttribute
	private final int y;
	
	public Field(FieldType type, int x, int y) {
		this.setType(type);
		this.owner = null;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return Der Besitzer, falls es keinen gibt, wird null zurueckgegeben
	 */
	public PlayerColor getOwner() {
		return owner;
	}

	/**
	 * @param Owner
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
	 * @param Der zu setzende Feldtyp
	 */
	public void setType(FieldType type) {
		this.type = type;
	}
	
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
   * @return x
   */
  public int getX() {
    return x;
  }

  /**
   * @return y
   */
  public int getY() {
    return y;
  }
  
  @Override
  public String toString() {
    return "Field: x = " + getX() + ", y = " + getY() + ", owner = " + this.getOwner() + ", type = " + this.getType();
  }

}
