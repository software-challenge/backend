package sc.plugin2014.entities;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.util.Constants;

public class Board {
    private final List<Field> fields = new ArrayList<Field>();

    public Board() {
        for (int i = 0; i < Constants.FIELDS_IN_X_DIM; i++) {
            for (int j = 0; j < Constants.FIELDS_IN_Y_DIM; j++) {
                fields.add(new Field(i, j));
            }
        }
    }

    public void layStone(Stone stone, int posX, int posY) {
        for (Field field : fields) {
            if ((field.getPosX() == posX) && (field.getPosY() == posY)) {
                field.setStone(stone);
                break;
            }
        }

        new InvalidMoveException("Feldpositionen außerhalb des Bereiches");
    }

    @Override
    public Object clone() {
        // TODO
        return null;
    }
}
