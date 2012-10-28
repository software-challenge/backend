package sc.plugin2014.util;

import java.util.*;
import java.util.Map.Entry;
import sc.plugin2014.entities.*;
import sc.plugin2014.laylogic.LayLogicFacade;
import sc.plugin2014.moves.LayMove;

public class GameUtil {

    /**
     * Prüft, ob der übergebene Zug regelkonform wäre.
     * 
     * @param laymove
     *            Der Legezug, welcher überprüft werden soll
     * @param board
     *            Das Spielbrett für das der Zug überprüft werden soll.
     * @return <code>true</code>: Der Zug wäre regelkonform <br />
     *         <code>false</code>: Der Zug wäre nicht regelkonform
     */
    public static boolean checkIfLayMoveIsValid(LayMove laymove, Board board) {
        try {
            LayLogicFacade
                    .checkIfLayMoveIsValid(laymove.getStoneToFieldMapping(),
                            board, !board.hasStones());
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Holt aus der übergebenen Liste von Steinen die Farbe der Steine, welche
     * am meisten vorhanden ist. Bei gleicher maximaler Anzahl mehrerer Farben
     * wird zufällig eine hiervon zurückgegeben.
     * 
     * @param stones
     *            Nicht leere Liste an Steinen
     * 
     * @return Steinfarbe, welche am meisten vorhanden ist
     */
    public static StoneColor getBestStoneColor(List<Stone> stones) {
        HashMap<StoneColor, Integer> seenColors = new HashMap<StoneColor, Integer>();
        for (Stone stone : stones) {
            if (seenColors.containsKey(stone.getColor())) {
                int colorCount = seenColors.get(stone.getColor()) + 1;
                seenColors.put(stone.getColor(), colorCount);
            }
            else {
                seenColors.put(stone.getColor(), 1);
            }
        }

        StoneColor bestStoneColor = null;
        int bestStoneColorCount = 0;

        for (Entry<StoneColor, Integer> seenColor : seenColors.entrySet()) {
            if (bestStoneColorCount < seenColor.getValue()) {
                bestStoneColor = seenColor.getKey();
                bestStoneColorCount = seenColor.getValue();
            }
        }
        return bestStoneColor;
    }

    /**
     * Holt aus der übergebenen Liste von Steinen die Form der Steine, welche
     * am meisten vorhanden ist. Bei gleicher maximaler Anzahl mehrerer Formen
     * wird zufällig eine hiervon zurückgegeben.
     * 
     * @param stones
     *            Nicht leere Liste an Steinen
     * 
     * @return Steinform, welche am meisten vorhanden ist
     */
    public static StoneShape getBestStoneShape(List<Stone> stones) {
        HashMap<StoneShape, Integer> seenShapes = new HashMap<StoneShape, Integer>();
        for (Stone stone : stones) {
            if (seenShapes.containsKey(stone.getShape())) {
                int shapeCount = seenShapes.get(stone.getShape()) + 1;
                seenShapes.put(stone.getShape(), shapeCount);
            }
            else {
                seenShapes.put(stone.getShape(), 1);
            }
        }

        StoneShape bestStoneShape = null;
        int bestStoneShapeCount = 0;

        for (Entry<StoneShape, Integer> seenShape : seenShapes.entrySet()) {
            if (bestStoneShapeCount < seenShape.getValue()) {
                bestStoneShape = seenShape.getKey();
                bestStoneShapeCount = seenShape.getValue();
            }
        }
        return bestStoneShape;
    }
}
