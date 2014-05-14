package sc.plugin2015.laylogic;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import sc.plugin2014.entities.*;
import sc.plugin2014.exceptions.InvalidMoveException;
import sc.plugin2014.moves.LayMove;
import sc.plugin2014.util.GameUtil;

public class LayLogicFacadeTest {

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfFirstLayMoveIsInvalid() throws InvalidMoveException {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field11 = board.getField(1, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field11);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
    }

    @Test
    public void testCheckIfLayMoveIsValid() {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone2, 0, 0);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }
    
    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidNotFreeFieldForum()
            throws InvalidMoveException {
        Board board = new Board();
        //steine wie im forum legen
        board.layStone(new Stone(StoneColor.BLUE, StoneShape.HEART), 4, 5);
        board.layStone(new Stone(StoneColor.BLUE, StoneShape.CLUBS), 5, 5);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.CLUBS), 5, 6);
        board.layStone(new Stone(StoneColor.BLUE, StoneShape.CLUBS), 6, 6);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.CLUBS), 6, 7);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.BELL), 7, 7);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.HEART), 8, 7);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.SPADES), 8, 8);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.SPADES), 9, 7);
        board.layStone(new Stone(StoneColor.GREEN, StoneShape.BELL), 9, 8);
        
       

        Stone stone = new Stone(StoneColor.YELLOW, StoneShape.CLUBS);

        //HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        //stoneToFieldMap.put(stone, board.getField(4, 5));
        LayMove lmove = new LayMove();
        lmove.layStoneOntoField(stone, board.getField(4, 5));
        boolean valid = GameUtil.checkIfLayMoveIsValid(lmove, board);
        System.out.println("Move is valid: " + valid);
        if(!valid){
        	throw new InvalidMoveException("Move not valid");
        }

        //LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidNoColorNoShape()
            throws InvalidMoveException {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.GREEN, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidSameColorSameShape()
            throws InvalidMoveException {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.ACORN);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidOnlyOneStone()
            throws InvalidMoveException {
        Board board = new Board();
        Field field00 = board.getField(0, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, true);
    }

    @Test
    public void testCheckIfLayMoveIsValidAdjoinedOneStone() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);

        Field field02 = board.getField(0, 2);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field02);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidAdjoinedWithGapOneStone()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);

        Field field03 = board.getField(0, 3);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field03);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidAdjoinedWithGapRightOneStone()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);

        Field field20 = board.getField(2, 0);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field20);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test
    public void testCheckIfLayMoveIsValidAdjoinedOneStoneToRight() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);

        Field field10 = board.getField(1, 0);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field10);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test
    public void testCheckIfLayMoveIsValidAdjoinedOneStoneInMiddle() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 2);

        Field field01 = board.getField(0, 1);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field01);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test
    public void testCheckIfLayMoveIsValidRowOverRow() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);
        board.layStone(stone3, 0, 2);

        Field field10 = board.getField(1, 0);
        Field field11 = board.getField(1, 1);
        Field field12 = board.getField(1, 2);

        Stone stone4 = new Stone(StoneColor.VIOLET, StoneShape.ACORN);
        Stone stone5 = new Stone(StoneColor.VIOLET, StoneShape.BELL);
        Stone stone6 = new Stone(StoneColor.VIOLET, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field10);
        stoneToFieldMap.put(stone5, field11);
        stoneToFieldMap.put(stone6, field12);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidRowOverRowButOneFails()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);
        board.layStone(stone3, 0, 2);

        Field field10 = board.getField(1, 0);
        Field field11 = board.getField(1, 1);
        Field field12 = board.getField(1, 2);

        Stone stone4 = new Stone(StoneColor.VIOLET, StoneShape.ACORN);
        Stone stone5 = new Stone(StoneColor.VIOLET, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.VIOLET, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field10);
        stoneToFieldMap.put(stone5, field11);
        stoneToFieldMap.put(stone6, field12);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test
    public void testCheckIfLayMoveIsValidLayOnOutside() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 1);
        board.layStone(stone2, 0, 2);
        board.layStone(stone3, 0, 3);

        Field field00 = board.getField(0, 0);
        Field field04 = board.getField(0, 4);

        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field00);
        stoneToFieldMap.put(stone5, field04);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidLayOnOutsideFail()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 1);
        board.layStone(stone2, 0, 2);
        board.layStone(stone3, 0, 3);

        Field field00 = board.getField(0, 0);
        Field field04 = board.getField(0, 4);

        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field00);
        stoneToFieldMap.put(stone5, field04);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test
    public void testCheckIfLayMoveIsValidLayARow() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);

        Field field02 = board.getField(0, 2);
        Field field12 = board.getField(1, 2);
        Field field22 = board.getField(2, 2);

        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.SPADES);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.ACORN);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field02);
        stoneToFieldMap.put(stone5, field12);
        stoneToFieldMap.put(stone6, field22);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidLayARowFail()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);

        Field field02 = board.getField(0, 2);
        Field field12 = board.getField(1, 2);
        Field field22 = board.getField(2, 2);

        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.SPADES);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field02);
        stoneToFieldMap.put(stone5, field12);
        stoneToFieldMap.put(stone6, field22);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidLayInNotSameDirection()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);

        board.layStone(stone, 0, 0);

        Field field01 = board.getField(0, 1);
        Field field10 = board.getField(1, 0);

        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.HEART);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone2, field01);
        stoneToFieldMap.put(stone3, field10);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test(expected = InvalidMoveException.class)
    public void testCheckIfLayMoveIsValidLayRowWithGap()
            throws InvalidMoveException {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 3);

        Field field01 = board.getField(0, 1);
        Field field04 = board.getField(0, 4);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field01);
        stoneToFieldMap.put(stone4, field04);

        LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
    }

    @Test
    public void testCheckIfLayMoveIsValidLayRowWithoutGap() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 2);

        Field field01 = board.getField(0, 1);
        Field field03 = board.getField(0, 3);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field01);
        stoneToFieldMap.put(stone4, field03);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }

    @Test
    public void testCheckIfLayMoveIsValidLayRowWithoutGapButRowHasMore() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stoneOutside = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 2);

        board.layStone(stoneOutside, 0, 5);

        Field field01 = board.getField(0, 1);
        Field field03 = board.getField(0, 3);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field01);
        stoneToFieldMap.put(stone4, field03);

        try {
            LayLogicFacade.checkIfLayMoveIsValid(stoneToFieldMap, board, false);
        }
        catch (InvalidMoveException e) {
            fail("Failed when it should not: " + e.getMessage());
        }
    }
}
