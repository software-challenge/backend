package sc.plugin2014.util;

import sc.plugin2014.entities.Board;
import sc.plugin2014.laylogic.LayLogicFacade;
import sc.plugin2014.moves.LayMove;

public class GameUtil {
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
}
