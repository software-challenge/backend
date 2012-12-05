package sc.plugin2014.laylogic;

import java.util.List;
import java.util.Map;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;

public class PointsCalculator {
    public static int getPointsForMove(Map<Stone, Field> stoneToFieldMapping,
            Board board) {
        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMapping, board,
                    !board.hasStones());
        }
        catch (InvalidMoveException ex) {
            return 0;
        }

        int result = 0;

        List<Field> describedRow = GetDescribedRow.getDescribedRow(
                stoneToFieldMapping.values(), board);

        if (describedRow.size() != 1) { // if 1 stone no real direction and only
                                        // adding through neighbours
            result += getPointsToAdd(describedRow.size());
        }

        for (Field field : stoneToFieldMapping.values()) {
            List<Field> neighbors = GetNeighbours.getOccupiedNeighbors(field,
                    board);
            boolean verticalDirectionDone = false;
            boolean horizonzalDirectionDone = false;
            if (!neighbors.isEmpty()) {
                for (Field neighbor : neighbors) {
                    if (neighbor.getPosX() == field.getPosX()) {
                        if (!verticalDirectionDone) {
                            verticalDirectionDone = true;
                        }
                        else {
                            continue;
                        }
                    }

                    if (neighbor.getPosY() == field.getPosY()) {
                        if (!horizonzalDirectionDone) {
                            horizonzalDirectionDone = true;
                        }
                        else {
                            continue;
                        }
                    }

                    List<Field> neighborRow = GetRowFromNeighbourFields
                            .getRowFromField(field, neighbor, board);
                    if (notPartOfDescribedRow(describedRow, neighborRow)) {
                        result += getPointsToAdd(neighborRow.size());
                    }
                }
            }
        }

        return result;
    }

    private static boolean notPartOfDescribedRow(List<Field> describedRow,
            List<Field> neighborRow) {
        for (Field neighborRowField : neighborRow) {
            boolean found = false;
            for (Field describedRowField : describedRow) {
                if (neighborRowField.equals(describedRowField)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return true;
            }
        }
        return false;
    }

    private static int getPointsToAdd(int donePoints) {
        if (donePoints == 6) {
            return 12;
        }
        else {
            return donePoints;
        }
    }
}
