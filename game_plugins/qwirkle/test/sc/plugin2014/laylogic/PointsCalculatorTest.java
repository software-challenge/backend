package sc.plugin2014.laylogic;

import static org.junit.Assert.*;
import java.util.HashMap;
import org.junit.Test;
import sc.plugin2014.entities.*;

public class PointsCalculatorTest {

    @Test
    public void testGetPointsForMoveSimple2() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(2, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple3() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);
        Field field02 = board.getField(0, 2);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.FOURSPIKES);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);
        stoneToFieldMap.put(stone3, field02);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(3, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple4() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);
        Field field02 = board.getField(0, 2);
        Field field03 = board.getField(0, 3);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.FOURSPIKES);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.RECTANGLE);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);
        stoneToFieldMap.put(stone3, field02);
        stoneToFieldMap.put(stone4, field03);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(4, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple5() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);
        Field field02 = board.getField(0, 2);
        Field field03 = board.getField(0, 3);
        Field field04 = board.getField(0, 4);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.FOURSPIKES);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.RECTANGLE);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.RHOMBUS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);
        stoneToFieldMap.put(stone3, field02);
        stoneToFieldMap.put(stone4, field03);
        stoneToFieldMap.put(stone5, field04);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(5, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimpleQwirkle() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(0, 1);
        Field field02 = board.getField(0, 2);
        Field field03 = board.getField(0, 3);
        Field field04 = board.getField(0, 4);
        Field field05 = board.getField(0, 5);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.FOURSPIKES);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.RECTANGLE);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.RHOMBUS);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.STAR);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);
        stoneToFieldMap.put(stone3, field02);
        stoneToFieldMap.put(stone4, field03);
        stoneToFieldMap.put(stone5, field04);
        stoneToFieldMap.put(stone6, field05);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(12, pointsForMove);
    }

    @Test
    public void testCheckIfLayMoveIsValidAdjoinedOneStoneInMiddle() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 2);

        Field field01 = board.getField(0, 1);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.FOURSPIKES);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field01);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(3, pointsForMove);
    }

    @Test
    public void testCheckIfLayMoveIsValidRowOverRow() {
        Board board = new Board();

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.CIRCLE);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.FLOWER);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.FOURSPIKES);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 1);
        board.layStone(stone3, 0, 2);

        Field field10 = board.getField(1, 0);
        Field field11 = board.getField(1, 1);
        Field field12 = board.getField(1, 2);

        Stone stone4 = new Stone(StoneColor.RED, StoneShape.CIRCLE);
        Stone stone5 = new Stone(StoneColor.RED, StoneShape.FLOWER);
        Stone stone6 = new Stone(StoneColor.RED, StoneShape.FOURSPIKES);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field10);
        stoneToFieldMap.put(stone5, field11);
        stoneToFieldMap.put(stone6, field12);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(9, pointsForMove);
    }
}
