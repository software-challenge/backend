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
    public Object clone() {
        // TODO
        return null;
    }

    public boolean hasStones() {
        for (Field field : fields) {
            if (!field.isFree()) {
                return true;
            }
        }
        return false;
    }
}
