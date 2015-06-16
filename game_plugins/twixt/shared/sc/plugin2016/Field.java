package sc.plugin2016;

import java.util.LinkedList;
import java.util.List;

public class Field {
	private Player owner;
	private FieldType type;
	private List<Field> connections;
	private final int x, y;
	
	public Field(FieldType type, int x, int y) {
		this.setType(type);
		this.setConnections(new LinkedList<Field>());
		this.owner = null;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the owner
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	/**
	 * @return the type
	 */
	public FieldType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	private void setType(FieldType type) {
		this.type = type;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Field) {
			Field f = (Field) o;
			return f.getOwner().equals(this.getOwner()) && f.getType().equals(this.getType());
		}
		return false;
	}
	
	public Field clone() {
		Field clone = new Field(this.getType(), this.getX(), this.getY());
		clone.setOwner(this.getOwner());
		return clone;
	}

  /**
   * Liefert die Liste der Felder, mit denen dieses Feld verbunden ist.
   * @return the connections
   */
  public List<Field> getConnections() {
    return connections;
  }
  
  /**
   * Nur fuer den Server relevante Methode zum hinzufügen einer neuen Verbindung.
   * @param connection hinzuzufuegende Verbindung
   */
  public void addConnection(Field connection) {
    this.connections.add(connection);
  }

  /**
   * @param connections the connections to set
   */
  private void setConnections(List<Field> connections) {
    this.connections = connections;
  }

  /**
   * @return the x
   */
  public int getX() {
    return x;
  }

  /**
   * @return the y
   */
  public int getY() {
    return y;
  }

}
