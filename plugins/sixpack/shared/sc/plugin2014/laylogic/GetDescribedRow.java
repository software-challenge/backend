package sc.plugin2014.laylogic;

import java.util.*;
import sc.plugin2014.entities.Board;
import sc.plugin2014.entities.Field;
import sc.plugin2014.util.Constants;

public class GetDescribedRow {
    protected static List<Field> getDescribedRow(Collection<Field> values,
            Board board) {
        List<Field> result = new ArrayList<Field>();

        result.addAll(values);

        boolean direction = getDirection(values);

        Field minField = findMinField(values, direction);

        walkIntoLeftDirection(board, result, direction, minField);

        Field maxField = findMaxField(values, direction);

        walkIntoRightDirection(board, result, direction, maxField);

        addFieldsBetween(board, result, direction, minField, maxField);

        return result;
    }

    private static boolean getDirection(Collection<Field> values) {
        boolean direction = LayLogicFacade.VERTICAL;
        int lastX = -1;
        int lastY = -1;
        for (Field field : values) {
            if (lastX == -1) {
                lastX = field.getPosX();
            }
            else {
                if (lastX == field.getPosX()) {
                    direction = LayLogicFacade.VERTICAL;
                    break;
                }
            }

            if (lastY == -1) {
                lastY = field.getPosY();
            }
            else {
                if (lastY == field.getPosY()) {
                    direction = LayLogicFacade.HORIZONTAL;
                    break;
                }
            }
        }
        return direction;
    }

    private static Field findMinField(Collection<Field> values,
            boolean direction) {
        Field min = null;
        for (Field field : values) {
            if (min == null) {
                min = field;
            }
            else {
                if (direction == LayLogicFacade.VERTICAL) {
                    if (min.getPosY() > field.getPosY()) {
                        min = field;
                    }
                }
                else {
                    if (min.getPosX() > field.getPosX()) {
                        min = field;
                    }
                }
            }
        }
        return min;
    }

    private static void walkIntoLeftDirection(Board board, List<Field> result,
            boolean direction, Field minField) {
        if (direction == LayLogicFacade.VERTICAL) {
            int i = minField.getPosY() - 1;
            while (i >= 0) {
                Field field = board.getField(minField.getPosX(), i);
                if (!field.isFree()) {
                    result.add(field);
                }
                else {
                    break;
                }
                i--;
            }
        }
        else {
            int i = minField.getPosX() - 1;
            while (i >= 0) {
                Field field = board.getField(i, minField.getPosY());
                if (!field.isFree()) {
                    result.add(field);
                }
                else {
                    break;
                }
                i--;
            }
        }
    }

    private static Field findMaxField(Collection<Field> values,
            boolean direction) {
        Field max = null;
        for (Field field : values) {
            if (max == null) {
                max = field;
            }
            else {
                if (direction == LayLogicFacade.VERTICAL) {
                    if (max.getPosY() < field.getPosY()) {
                        max = field;
                    }
                }
                else {
                    if (max.getPosX() < field.getPosX()) {
                        max = field;
                    }
                }
            }
        }
        return max;
    }

    private static void walkIntoRightDirection(Board board, List<Field> result,
            boolean direction, Field maxField) {
        if (direction == LayLogicFacade.VERTICAL) {
            int i = maxField.getPosY() + 1;
            while (i < Constants.FIELDS_IN_Y_DIM) {
                Field field = board.getField(maxField.getPosX(), i);
                if (!field.isFree()) {
                    result.add(field);
                }
                else {
                    break;
                }
                i++;
            }
        }
        else {
            int i = maxField.getPosX() + 1;
            while (i < Constants.FIELDS_IN_X_DIM) {
                Field field = board.getField(i, maxField.getPosY());
                if (!field.isFree()) {
                    result.add(field);
                }
                else {
                    break;
                }
                i++;
            }
        }
    }

    private static void addFieldsBetween(Board board, List<Field> result,
            boolean direction, Field minField, Field maxField) {
        if (direction == LayLogicFacade.VERTICAL) {
            int i = minField.getPosY();
            while (i < maxField.getPosY()) {
                Field field = board.getField(minField.getPosX(), i);
                if (!field.isFree()) {
                    result.add(field);
                }
                i++;
            }
        }
        else {
            int i = minField.getPosX();
            while (i < maxField.getPosX()) {
                Field field = board.getField(i, minField.getPosY());
                if (!field.isFree()) {
                    result.add(field);
                }
                i++;
            }
        }
    }
}
