package sc.plugin2014.entities;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Repräsentiert einen Spielstein.
 * 
 * @author ffi
 * 
 */
@XStreamAlias(value = "stone")
public class Stone implements Cloneable {

	@XStreamAsAttribute
	private final StoneColor color;

	@XStreamAsAttribute
	private final StoneShape shape;

	@XStreamAsAttribute
	private final int identifier;

	/**
	 * Erzeugt einen Spielstein mit identifier=0, color= StoneColor.BLUE,
	 * shape=StoneShape.ACORN
	 */
	public Stone() {
		identifier = 0;
		color = StoneColor.BLUE;
		shape = StoneShape.ACORN;
	}

	public Stone(StoneColor color, StoneShape shape) {
		identifier = StoneIdentifierGenerator.getNextId();
		this.color = color;
		this.shape = shape;
	}

	/**
	 * Liefert die Farbe des Spielsteines
	 * @return Spielsteinfarbe
	 */
	public StoneColor getColor() {
		return color;
	}

	/**
	 * Liefert die Form des Spielsteines
	 * @return Spielsteinform
	 */
	public StoneShape getShape() {
		return shape;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Stone)
				&& (((Stone) obj).identifier == identifier)
				&& (((Stone) obj).color == color)
				&& (((Stone) obj).shape == shape);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String toString() {
		return "Stone - Color: " + color + " - Shape: " + shape
				+ " - Identifier: " + identifier;
	}
}
