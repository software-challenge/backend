package sc.plugin2014.entities;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.util.Constants;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "board")
public class Board implements Cloneable {

	@XStreamImplicit(itemFieldName = "field")
	private final List<Field> fields;

	public Board() {
		fields = new ArrayList<Field>();

		for (int i = 0; i < Constants.FIELDS_IN_X_DIM; i++) {
			for (int j = 0; j < Constants.FIELDS_IN_Y_DIM; j++) {
				fields.add(new Field(i, j));
			}
		}
	}

	public List<Field> getFields() {
		return fields;
	}

	public void layStone(Stone stone, int posX, int posY) {
		if (stone == null) {
			throw new IllegalArgumentException("Stein darf nicht leer sein");
		}

		Field field = getField(posX, posY);
		field.setStone(stone);
	}

	public Field getField(int posX, int posY) {
		for (Field field : fields) {
			if ((field.getPosX() == posX) && (field.getPosY() == posY)) {
				return field;
			}
		}

		throw new IllegalArgumentException(
				"Feldpositionen außerhalb des Bereiches");
	}

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
			for(Field field: fields){
				if(field.isFree()){
					if(!((Board) obj).getField(field.getPosX(), field.getPosY()).isFree()){
						return false;
					}
				}else {
					if(!field.getStone().equals(((Board) obj).getField(field.getPosX(), field.getPosY()).getStone())){
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
