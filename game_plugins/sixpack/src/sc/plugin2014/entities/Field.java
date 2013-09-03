package sc.plugin2014.entities;

import java.awt.Point;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;

/**
 * Stellt ein Spieldfeld dar. Besitzt eine x- und y-Position, sowie eventuell einen Stein.
 * @author ffi
 *
 */
@XStreamAlias(value = "field")
public class Field implements Cloneable {

	@XStreamAsAttribute
	private int posX;

	@XStreamAsAttribute
	private int posY;

	@XStreamAsAttribute
	private Stone stone;

	public Field() {
	}

	public Field(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	/** Liefert die x-Position zurück
	 * @return x-Position
	 */
	public int getPosX() {
		return posX;
	}

	/** Liefert die y-Position zurück
	 * @return y-Position
	 */
	public int getPosY() {
		return posY;
	}

	/** Liefert die Position als {@link Point} zurück
	 * @return
	 */
	public Point getPosAsPoint() {
		return new Point(posX, posY);
	}

	public Stone getStone() {
		return stone;
	}

	public void setStone(Stone stone) {
		this.stone = stone;
	}

	public boolean isFree() {
		return getStone() == null;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Field) {
			Field otherField = (Field) o;
			return (otherField.getPosX() == getPosX())
					&& (otherField.getPosY() == getPosY());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Field: (" + getPosX() + ", " + getPosY() + ")";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Field clone = new Field(this.posX, this.posY);
		if(stone != null){
			clone.setStone((Stone) stone.clone());
		}		
		return clone;
	}
}
