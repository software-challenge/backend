package sc.plugin2014.util;

import java.util.*;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;

public class GameUtil {

    public static void checkIfLayMoveIsValid(
            Map<Stone, Field> stoneToFieldMapping, Board board,
            boolean firstLayTurn) throws InvalidMoveException {
        checkIfFieldsAreFree(stoneToFieldMapping.values(), board.getFields());

        if (firstLayTurn) {
            checkIfValidRow(stoneToFieldMapping);
        }
        else {
            checkAllPossibleRows(stoneToFieldMapping, board.getFields());
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

        checkValidColorOrShape(stoneToFieldMapping.keySet());
    }

    private static void checkCoherentFields(Collection<Field> fields)
            throws InvalidMoveException {
        boolean valid = checkVerticalRow(fields);
        if (valid) {
            return;
        }

        valid = checkHorizontalRow(fields);
        if (valid) {
            return;
        }

        throw new InvalidMoveException("Steine bilden keine Reihe");
    }

    private static boolean checkVerticalRow(Collection<Field> fields) {
        List<Integer> numbers = new ArrayList<Integer>();
        for (Field field : fields) {
            numbers.add(field.getPosY());
        }

        return checkCoherentNumbers(numbers);
    }

    private static boolean checkCoherentNumbers(List<Integer> numbers) {
        Collections.sort(numbers);

        int lastNumber = -1;
        for (int i = 0; i < numbers.size(); i++) {
            int currentNumber = numbers.get(i);
            if (lastNumber == -1) {
                lastNumber = currentNumber;
            }
            else {
                if (currentNumber != (lastNumber + 1)) {
                    return false;
                }
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

    private static void checkValidColorOrShape(Set<Stone> stones)
            throws InvalidMoveException {
        boolean valid = checkValidColorInRow(stones);
        if (valid) {
            return;
        }

        valid = checkValidShapeInRow(stones);
        if (valid) {
            return;
        }

        throw new InvalidMoveException(
                "In der Reihe sind weder Farbe noch Form einheitlich");
    }

    private static boolean checkValidColorInRow(Set<Stone> stones) {
        boolean seenBlue = false;
        boolean seenGreen = false;
        boolean seenOrange = false;
        boolean seenPurple = false;
        boolean seenRed = false;
        boolean seenYellow = false;

        for (Stone stone : stones) {
            switch (stone.getColor()) {
                case BLUE:
                    if (seenBlue) {
                        return false;
                    }
                    else {
                        seenBlue = true;
                    }
                    break;
                case GREEN:
                    if (seenGreen) {
                        return false;
                    }
                    else {
                        seenGreen = true;
                    }
                    break;
                case ORANGE:
                    if (seenOrange) {
                        return false;
                    }
                    else {
                        seenOrange = true;
                    }
                    break;
                case PURPLE:
                    if (seenPurple) {
                        return false;
                    }
                    else {
                        seenPurple = true;
                    }
                    break;
                case RED:
                    if (seenRed) {
                        return false;
                    }
                    else {
                        seenRed = true;
                    }
                    break;
                case YELLOW:
                    if (seenYellow) {
                        return false;
                    }
                    else {
                        seenYellow = true;
                    }
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    private static boolean checkValidShapeInRow(Set<Stone> stones) {
        boolean seenCircle = false;
        boolean seenFlower = false;
        boolean seenFourSpikes = false;
        boolean seenRhombus = false;
        boolean seenSquare = false;
        boolean seenStar = false;

        for (Stone stone : stones) {
            switch (stone.getShape()) {
                case CIRCLE:
                    if (seenCircle) {
                        return false;
                    }
                    else {
                        seenCircle = true;
                    }
                    break;
                case FLOWER:
                    if (seenFlower) {
                        return false;
                    }
                    else {
                        seenFlower = true;
                    }
                    break;
                case FOUR_SPIKE:
                    if (seenFourSpikes) {
                        return false;
                    }
                    else {
                        seenFourSpikes = true;
                    }
                    break;
                case RHOMBUS:
                    if (seenRhombus) {
                        return false;
                    }
                    else {
                        seenRhombus = true;
                    }
                    break;
                case SQUARE:
                    if (seenSquare) {
                        return false;
                    }
                    else {
                        seenSquare = true;
                    }
                    break;
                case STAR:
                    if (seenStar) {
                        return false;
                    }
                    else {
                        seenStar = true;
                    }
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    private static void checkAllPossibleRows(
            Map<Stone, Field> stoneToFieldMapping, List<Field> fields) {
        // TODO Auto-generated method stub

    }
}
