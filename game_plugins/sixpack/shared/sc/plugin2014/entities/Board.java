package sc.plugin2014.entities;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Das Board repräsentiert das Spielbrett auf dem Spielsteine ausgelegt werden
 * können. Beherbergt eine Menge von {@link Field Feldern} welche entweder leer
 * sind, oder auf denen ein {@link Stone Stein} liegt.
 * 
 * @author ffi
 * 
 */
@XStreamAlias(value = "board")
public class Board implements Cloneable {

	@XStreamImplicit(itemFieldName = "field")
	private final List<Field> fields;

	/**
	 * Erzeugt ein neues Board. Dabei werden die {@link Field Spielfelder}
	 * generiert.
	 */
	public Board() {
		fields = new ArrayList<Field>();

		for (int i = 0; i < Constants.FIELDS_IN_X_DIM; i++) {
			for (int j = 0; j < Constants.FIELDS_IN_Y_DIM; j++) {
				fields.add(new Field(i, j));
			}
		}
	}

	/**
	 * Liefert eine Liste mit allen Feldern zurück
	 * 
	 * @return Liste mit Feldern
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * Legt einen Spielstein auf das Feld an Position (x,y)
	 * 
	 * @param stone
	 * @param posX
	 * @param posY
	 */
	public void layStone(Stone stone, int posX, int posY) {
		if (stone == null) {
			throw new IllegalArgumentException("Stein darf nicht leer sein");
		}

		Field field = getField(posX, posY);
		field.setStone(stone);
	}

	/**
	 * Liefert das Feld an Position (posX,posY) zurück.
	 * 
	 * @param posX
	 * @param posY
	 * @return
	 */
	public Field getField(int posX, int posY) {
		for (Field field : fields) {
			if ((field.getPosX() == posX) && (field.getPosY() == posY)) {
				return field;
			}
		}

		throw new IllegalArgumentException(
				"Feldpositionen außerhalb des Bereiches");
	}

	/**
	 * Klont dieses Objekt. (deep-copy)
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Board clone = new Board();
		for (Field field : fields) {
			if (!field.isFree()) {
				clone.layStone((Stone) field.getStone().clone(),
						field.getPosX(), field.getPosY());
			}
		}
		return clone;
	}

	/**
	 * Überprüft ob irgendein Feld mit einem Stein belegt ist.
	 * 
	 * @return true, wenn irgendein Feld mit einem Spielstein belegt ist
	 */
	public boolean hasStones() {
		for (Field field : fields) {
			if (!field.isFree()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Board) {
			for (Field field : fields) {
				if (field.isFree()) {
					if (!((Board) obj).getField(field.getPosX(),
							field.getPosY()).isFree()) {
						return false;
					}
				} else {
					if (!field.getStone().equals(
							((Board) obj).getField(field.getPosX(),
									field.getPosY()).getStone())) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
