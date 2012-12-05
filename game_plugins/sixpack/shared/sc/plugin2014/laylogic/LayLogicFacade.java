package sc.plugin2014.laylogic;

import java.util.*;
import java.util.Map.Entry;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;

public class LayLogicFacade {

    public static final boolean VERTICAL   = false;
    public static final boolean HORIZONTAL = true;

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
            if (hasField(boardFields, passedField)) {
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

    private static boolean hasField(List<Field> boardFields, Field passedField) {
        for (Field field : boardFields) {
            if (field.equals(passedField)) {
                return true;
            }
        }
        return false;
    }

    private static void checkIfValidRow(Map<Stone, Field> stoneToFieldMapping)
            throws InvalidMoveException {
        checkCoherentFields(stoneToFieldMapping.values());

        BasicShapeAndColorChecker.checkValidColorOrShape(new ArrayList<Stone>(
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
        int xDim = -1;
        for (Field field : fields) {
            if (xDim == -1) {
                xDim = field.getPosX();
            }

            if (xDim != field.getPosX()) {
                return false;
            }

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
        int yDim = -1;
        for (Field field : fields) {
            if (yDim == -1) {
                yDim = field.getPosY();
            }

            if (yDim != field.getPosY()) {
                return false;
            }
            numbers.add(field.getPosX());
        }

        return checkCoherentNumbers(numbers);
    }

    private static void checkAllPossibleRows(
            Map<Stone, Field> stoneToFieldMapping, Board board)
            throws InvalidMoveException {
        boolean adjoinedFound = false;

        if (stoneToFieldMapping.size() >= 2) {
            checkSameDirectionOfStones(stoneToFieldMapping);

            List<Field> describedRow = GetDescribedRow.getDescribedRow(
                    stoneToFieldMapping.values(), board);
            checkCoherentFields(describedRow);
            List<Stone> stonesOfDescribedRow = getStonesFromFields(
                    stoneToFieldMapping, describedRow, board);

            BasicShapeAndColorChecker
                    .checkValidColorOrShape(stonesOfDescribedRow);
        }

        for (Field field : stoneToFieldMapping.values()) {
            List<Field> neighbors = GetNeighbours.getOccupiedNeighbors(field,
                    board);
            if (!neighbors.isEmpty()) {
                adjoinedFound = true;

                for (Field neighbor : neighbors) {
                    List<Field> neighborRow = GetRowFromNeighbourFields
                            .getRowFromField(field, neighbor, board);
                    List<Stone> stonesRow = getStonesFromFields(
                            stoneToFieldMapping, neighborRow, board);
                    BasicShapeAndColorChecker.checkValidColorOrShape(stonesRow);
                }
            }
        }

        if (!adjoinedFound) {
            throw new InvalidMoveException(
                    "Steine können nur an andere Reihen angelegt werden");
        }
    }

    private static void checkSameDirectionOfStones(
            Map<Stone, Field> stoneToFieldMapping) throws InvalidMoveException {
        boolean sameDirection = true;

        Collection<Field> fields = stoneToFieldMapping.values();

        int lastX = -1;
        for (Field field : fields) {
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
        for (Field field : fields) {
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

}
