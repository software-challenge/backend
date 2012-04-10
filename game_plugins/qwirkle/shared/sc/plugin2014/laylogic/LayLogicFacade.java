package sc.plugin2014.laylogic;

import java.util.*;
import java.util.Map.Entry;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.util.Constants;

public class LayLogicFacade {

    private static final boolean VERTICAL   = false;
    private static final boolean HORIZONTAL = true;

    public static void checkIfLayMoveIsValid(
            Map<Stone, Field> stoneToFieldMapping, Board board,
            boolean firstLayTurn) throws InvalidMoveException {
        checkIfFieldsAreFree(stoneToFieldMapping.values(), board.getFields());

        if (firstLayTurn) {
            if (stoneToFieldMapping.size() == 1) {
                throw new InvalidMoveException(
                        "Nur einen Stein als Anfang gelegt");
            }
            checkIfValidRow(stoneToFieldMapping);
        }
        else {
            checkAllPossibleRows(stoneToFieldMapping, board);
        }
    }

    private static void checkIfFieldsAreFree(Collection<Field> passedFields,
            List<Field> boardFields) throws InvalidMoveException {
        for (Field passedField : passedFields) {
            if (boardFields.contains(passedField)) {
                if (passedField.isFree()) {
                    continue;
                }
                else {
                    throw new InvalidMoveException("Feld ist belegt");
                }
            }
            else {
                throw new InvalidMoveException(
                        "Feld ist nicht im Spielbrett vorhanden");
            }
        }
    }

    private static void checkIfValidRow(Map<Stone, Field> stoneToFieldMapping)
            throws InvalidMoveException {
        checkCoherentFields(stoneToFieldMapping.values());

        checkValidColorOrShape(new ArrayList<Stone>(
                stoneToFieldMapping.keySet()));
    }

    private static void checkCoherentFields(Collection<Field> fields)
            throws InvalidMoveException {
        if (!checkVerticalRow(fields) && !checkHorizontalRow(fields)) {
            throw new InvalidMoveException("Steine bilden keine Reihe");
        }
    }

    private static boolean checkVerticalRow(Collection<Field> fields) {
        List<Integer> numbers = new ArrayList<Integer>();
        for (Field field : fields) {
            numbers.add(field.getPosY());
        }

        return checkCoherentNumbers(numbers);
    }

    private static boolean checkCoherentNumbers(List<Integer> numbers) {
        if (numbers.size() == 0) {
            return false;
        }

        Collections.sort(numbers);

        int lastNumber = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i) != (lastNumber + 1)) {
                return false;
            }
            else {
                lastNumber = numbers.get(i);
            }
        }

        return true;
    }

    private static boolean checkHorizontalRow(Collection<Field> fields) {
        List<Integer> numbers = new ArrayList<Integer>();
        for (Field field : fields) {
            numbers.add(field.getPosX());
        }

        return checkCoherentNumbers(numbers);
    }

    private static void checkValidColorOrShape(List<Stone> stonesRow)
            throws InvalidMoveException {
        if (!checkValidColorInRow(stonesRow)
                && !checkValidShapeInRow(stonesRow)) {
            throw new InvalidMoveException("Keine valide Reihe"); // TODO add
                                                                  // row desc
        }
    }

    private static boolean checkValidColorInRow(List<Stone> stonesRow) {
        List<StoneColor> seenColors = new ArrayList<StoneColor>();
        List<StoneShape> seenShapes = new ArrayList<StoneShape>();

        for (Stone stone : stonesRow) {
            if (!checkForSameColor(seenColors, stone.getColor())
                    || !checkForDifferentShapes(seenShapes, stone.getShape())) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkForSameColor(List<StoneColor> seenColors,
            StoneColor stoneColor) {
        if (!seenColors.contains(stoneColor)) {
            seenColors.add(stoneColor);
            if (seenColors.size() > 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkForDifferentShapes(List<StoneShape> seenShapes,
            StoneShape stoneShape) {
        if (seenShapes.contains(stoneShape)) {
            return false;
        }
        else {
            seenShapes.add(stoneShape);
        }
        return true;
    }

    private static boolean checkValidShapeInRow(List<Stone> stonesRow) {
        List<StoneShape> seenShapes = new ArrayList<StoneShape>();
        List<StoneColor> seenColors = new ArrayList<StoneColor>();

        for (Stone stone : stonesRow) {
            if (!checkForSameShape(seenShapes, stone.getShape())
                    || !checkForDifferentColors(seenColors, stone.getColor())) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkForSameShape(List<StoneShape> seenShapes,
            StoneShape stoneShape) {
        if (!seenShapes.contains(stoneShape)) {
            seenShapes.add(stoneShape);
            if (seenShapes.size() > 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkForDifferentColors(List<StoneColor> seenColors,
            StoneColor stoneColor) {
        if (seenColors.contains(stoneColor)) {
            return false;
        }
        else {
            seenColors.add(stoneColor);
        }
        return true;
    }

    private static void checkAllPossibleRows(
            Map<Stone, Field> stoneToFieldMapping, Board board)
            throws InvalidMoveException {
        boolean adjoinedFound = false;

        if (stoneToFieldMapping.size() >= 2) {
            checkSameDirectionOfStones(stoneToFieldMapping);

            List<Field> describedRow = getDescribedRow(
                    stoneToFieldMapping.values(), board);
            checkCoherentFields(describedRow);
            List<Stone> stonesOfDescribedRow = getStonesFromFields(
                    stoneToFieldMapping, describedRow, board);

            checkValidColorOrShape(stonesOfDescribedRow);
        }

        for (Field field : stoneToFieldMapping.values()) {
            List<Field> neighbors = getOccupiedNeighbors(field, board);
            if (!neighbors.isEmpty()) {
                adjoinedFound = true;

                for (Field neighbor : neighbors) {
                    List<Field> neighborRow = getRowFromField(field, neighbor,
                            board);
                    List<Stone> stonesRow = getStonesFromFields(
                            stoneToFieldMapping, neighborRow, board);
                    checkValidColorOrShape(stonesRow);
                }
            }
        }

        if (!adjoinedFound) {
            throw new InvalidMoveException(
                    "Steine können nur an andere Reihen angelegt werden");
        }
    }

    private static List<Field> getDescribedRow(Collection<Field> values,
            Board board) throws InvalidMoveException {
        List<Field> result = new ArrayList<Field>();

        result.addAll(values);

        boolean direction = getDirection(values);

        Field minField = findMinField(values, direction);
        Field maxField = findMaxField(values, direction);

        if (direction == VERTICAL) {
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

        return result;
    }

    private static Field findMinField(Collection<Field> values,
            boolean direction) {
        Field min = null;
        for (Field field : values) {
            if (direction == VERTICAL) {
                if (min == null) {
                    min = field;
                }
                else {
                    if (min.getPosY() > field.getPosY()) {
                        min = field;
                    }
                }
            }
            else {
                if (min == null) {
                    min = field;
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

    private static Field findMaxField(Collection<Field> values,
            boolean direction) {
        Field max = null;
        for (Field field : values) {
            if (direction == VERTICAL) {
                if (max == null) {
                    max = field;
                }
                else {
                    if (max.getPosY() < field.getPosY()) {
                        max = field;
                    }
                }
            }
            else {
                if (max == null) {
                    max = field;
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

    private static boolean getDirection(Collection<Field> values) {
        boolean direction = VERTICAL;
        int lastX = -1;
        int lastY = -1;
        for (Field field : values) {
            if (lastX == -1) {
                lastX = field.getPosX();
            }
            else {
                if (lastX == field.getPosX()) {
                    direction = VERTICAL;
                    break;
                }
            }

            if (lastY == -1) {
                lastY = field.getPosY();
            }
            else {
                if (lastY == field.getPosY()) {
                    direction = HORIZONTAL;
                    break;
                }
            }
        }
        return direction;
    }

    private static void checkSameDirectionOfStones(
            Map<Stone, Field> stoneToFieldMapping) throws InvalidMoveException {
        boolean sameDirection = true;

        int lastX = -1;
        for (Field field : stoneToFieldMapping.values()) {
            if (lastX == -1) {
                lastX = field.getPosX();
            }
            else {
                if (lastX != field.getPosX()) {
                    sameDirection = false;
                    break;
                }
            }
        }

        if (sameDirection) {
            return;
        }

        sameDirection = true;

        int lastY = -1;
        for (Field field : stoneToFieldMapping.values()) {
            if (lastY == -1) {
                lastY = field.getPosY();
            }
            else {
                if (lastY != field.getPosY()) {
                    sameDirection = false;
                    break;
                }
            }
        }

        if (sameDirection) {
            return;
        }

        throw new InvalidMoveException("Steine müssen eine Reihe formen");
    }

    private static List<Stone> getStonesFromFields(
            Map<Stone, Field> stoneToFieldMapping, List<Field> neighborRow,
            Board board) {
        List<Stone> result = new ArrayList<Stone>();
        for (Field field : neighborRow) {
            if (stoneToFieldMapping.containsValue(field)) {
                for (Entry<Stone, Field> entry : stoneToFieldMapping.entrySet()) {
                    if (entry.getValue() == field) {
                        result.add(entry.getKey());
                        break;
                    }
                }
            }
            else {
                result.add(field.getStone());
            }
        }

        return result;
    }

    private static List<Field> getRowFromField(Field field, Field neighbor,
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

    private static List<Field> getOccupiedNeighbors(Field field, Board board) {
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
