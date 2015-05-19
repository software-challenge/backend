package sc.plugin2016;

public class Field {
	private Player owner;
	private FieldType type;
	
	public Field(FieldType type) {
		this.setType(type);
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
		Field clone = new Field(this.getType());
		clone.setOwner(this.getOwner());
		return clone;
	}
	
}
