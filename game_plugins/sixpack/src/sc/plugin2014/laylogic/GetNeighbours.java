package sc.plugin2014.laylogic;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.entities.Board;
import sc.plugin2014.entities.Field;
import sc.plugin2014.util.Constants;

public class GetNeighbours {
    public static List<Field> getOccupiedNeighbors(Field field, Board board) {
        List<Field> result = new ArrayList<Field>();

        int leftField = field.getPosX() - 1;
        if (leftField >= 0) {
            addIfOccupied(leftField, field.getPosY(), board, result);
        }

        int rightField = field.getPosX() + 1;
        if (rightField < Constants.FIELDS_IN_X_DIM) {
            addIfOccupied(rightField, field.getPosY(), board, result);
        }

        int upperField = field.getPosY() - 1;
        if (upperField >= 0) {
            addIfOccupied(field.getPosX(), upperField, board, result);
        }

        int downField = field.getPosY() + 1;
        if (downField < Constants.FIELDS_IN_Y_DIM) {
            addIfOccupied(field.getPosX(), downField, board, result);
        }
        return result;
    }

    private static void addIfOccupied(int posX, int posY, Board board,
            List<Field> result) {
        Field possibleNeighbor = board.getField(posX, posY);
        if (!possibleNeighbor.isFree()) {
            result.add(possibleNeighbor);
        }
    }
}
