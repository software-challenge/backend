package sc.plugin2014.util;

import static org.junit.Assert.*;
import java.util.HashMap;
import org.junit.Test;
import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;

public class GameUtilTest {

    @Test
    public void testCheckIfLayMoveIsValid() {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        try {
            GameUtil.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not");
        }
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidNotFreeField()
            throws InvalidMoveException {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);

        board.layStone(stone2, 0, 0);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        GameUtil.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
    }
}
