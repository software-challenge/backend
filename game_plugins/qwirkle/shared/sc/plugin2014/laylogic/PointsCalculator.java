package sc.plugin2014.laylogic;

import java.util.List;
import java.util.Map;
import sc.plugin2014.entities.*;

public class PointsCalculator {
    public static int getPointsForMove(Map<Stone, Field> stoneToFieldMapping,
            Board board) {
        // TODO check if lay move is valid at first

        int result = 0;

        List<Field> describedRow = GetDescribedRow.getDescribedRow(
                stoneToFieldMapping.values(), board);

        result += getPointsToAdd(describedRow.size());

        for (Field field : stoneToFieldMapping.values()) {
            List<Field> neighbors = GetNeighbours.getOccupiedNeighbors(field,
                    board);
            if (!neighbors.isEmpty()) {
                for (Field neighbor : neighbors) {
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
