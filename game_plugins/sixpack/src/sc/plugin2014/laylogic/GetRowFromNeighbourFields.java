package sc.plugin2014.laylogic;

import java.util.ArrayList;
import java.util.List;
import sc.plugin2014.entities.Board;
import sc.plugin2014.entities.Field;
import sc.plugin2014.util.Constants;

public class GetRowFromNeighbourFields {
    public static List<Field> getRowFromField(Field field, Field neighbor,
            Board board) {
        List<Field> result = new ArrayList<Field>();
        result.add(field);

        if (field.getPosX() == neighbor.getPosX()) {
            int i = field.getPosY() - 1;
            while (i >= 0) {
                Field possRowField = board.getField(field.getPosX(), i);
                if (possRowField.isFree()) {
                    break;
                }
                else {
                    result.add(possRowField);
                    i--;
                }
            }

            i = field.getPosY() + 1;
            while (i < Constants.FIELDS_IN_Y_DIM) {
                Field possRowField = board.getField(field.getPosX(), i);
                if (possRowField.isFree()) {
                    break;
                }
                else {
                    result.add(possRowField);
                    i++;
                }
            }
        }
        else {
            int i = field.getPosX() - 1;
            while (i >= 0) {
                Field possRowField = board.getField(i, field.getPosY());
                if (possRowField.isFree()) {
                    break;
                }
                else {
                    result.add(possRowField);
                    i--;
                }
            }

            i = field.getPosX() + 1;
            while (i < Constants.FIELDS_IN_X_DIM) {
                Field possRowField = board.getField(i, field.getPosY());
                if (possRowField.isFree()) {
                    break;
                }
                else {
                    result.add(possRowField);
                    i++;
                }
            }
        }

        return result;
    }
}
