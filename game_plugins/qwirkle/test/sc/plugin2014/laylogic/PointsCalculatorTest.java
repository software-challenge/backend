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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);

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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);

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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

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

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        board.layStone(stone, 0, 0);
        board.layStone(stone2, 0, 2);

        Field field01 = board.getField(0, 1);

        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field01);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(3, pointsForMove);
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

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(9, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple2Vertical() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(2, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple3Vertical() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone, field00);
        stoneToFieldMap.put(stone2, field01);
        stoneToFieldMap.put(stone3, field02);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(3, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple4Vertical() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);
        Field field03 = board.getField(3, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);

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
    public void testGetPointsForMoveSimple5Vertical() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);
        Field field03 = board.getField(3, 0);
        Field field04 = board.getField(4, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);

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
    public void testGetPointsForMoveSimpleQwirkleVertical() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);
        Field field03 = board.getField(3, 0);
        Field field04 = board.getField(4, 0);
        Field field05 = board.getField(5, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

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
    public void testGetPointsForMoveSimpleQwirkleVerticalIn2Steps()
            throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);
        Field field03 = board.getField(3, 0);
        Field field04 = board.getField(4, 0);
        Field field05 = board.getField(5, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        field00.setStone(stone);
        field01.setStone(stone2);
        field02.setStone(stone3);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone4, field03);
        stoneToFieldMap.put(stone5, field04);
        stoneToFieldMap.put(stone6, field05);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(12, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple3VerticalIn2Steps() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);

        field00.setStone(stone);
        field01.setStone(stone2);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field02);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(3, pointsForMove);
    }

    @Test
    public void testGetPointsForMoveSimple4VerticalIn2Steps() throws Exception {
        Board board = new Board();
        Field field00 = board.getField(0, 0);
        Field field01 = board.getField(1, 0);
        Field field02 = board.getField(2, 0);
        Field field03 = board.getField(3, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);

        field00.setStone(stone);
        field01.setStone(stone2);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone3, field02);
        stoneToFieldMap.put(stone4, field03);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(4, pointsForMove);
    }

    @Test
    public void testGetPointsComplexMove() throws Exception {
        Board board = new Board();
        Field field01 = board.getField(0, 1);
        Field field11 = board.getField(1, 1);
        Field field21 = board.getField(2, 1);
        Field field31 = board.getField(3, 1);
        Field field41 = board.getField(4, 1);
        Field field51 = board.getField(5, 1);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        field01.setStone(stone);
        field11.setStone(stone2);
        field21.setStone(stone3);
        field41.setStone(stone4);
        field51.setStone(stone6);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone5, field31);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(12, pointsForMove);
    }

    @Test
    public void testGetPointsComplexMove2() throws Exception {
        Board board = new Board();
        Field field01 = board.getField(0, 1);
        Field field11 = board.getField(1, 1);
        Field field21 = board.getField(2, 1);
        Field field31 = board.getField(3, 1);
        Field field41 = board.getField(4, 1);
        Field field51 = board.getField(5, 1);

        Field field30 = board.getField(3, 0);
        Field field32 = board.getField(3, 2);
        Field field33 = board.getField(3, 3);
        Field field34 = board.getField(3, 4);
        Field field35 = board.getField(3, 5);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        Stone stone7 = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone8 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone9 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone10 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone11 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        field01.setStone(stone);
        field11.setStone(stone2);
        field21.setStone(stone3);
        field41.setStone(stone4);
        field51.setStone(stone6);

        field30.setStone(stone7);
        field32.setStone(stone8);
        field33.setStone(stone9);
        field34.setStone(stone10);
        field35.setStone(stone11);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone5, field31);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(24, pointsForMove);
    }

    @Test
    public void testGetPointsComplexMove3() throws Exception {
        Board board = new Board();
        Field field01 = board.getField(0, 1);
        Field field11 = board.getField(1, 1);
        Field field21 = board.getField(2, 1);
        Field field31 = board.getField(3, 1);
        Field field41 = board.getField(4, 1);
        Field field51 = board.getField(5, 1);

        Field field32 = board.getField(3, 2);
        Field field33 = board.getField(3, 3);
        Field field34 = board.getField(3, 4);
        Field field35 = board.getField(3, 5);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        Stone stone8 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone9 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone10 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone11 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        field01.setStone(stone);
        field11.setStone(stone2);
        field21.setStone(stone3);
        field41.setStone(stone4);
        field51.setStone(stone6);

        field32.setStone(stone8);
        field33.setStone(stone9);
        field34.setStone(stone10);
        field35.setStone(stone11);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone5, field31);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(17, pointsForMove);
    }

    @Test
    public void testGetPointsComplexMove4() throws Exception {
        Board board = new Board();
        Field field01 = board.getField(0, 1);
        Field field11 = board.getField(1, 1);
        Field field21 = board.getField(2, 1);
        Field field31 = board.getField(3, 1);
        Field field41 = board.getField(4, 1);
        Field field51 = board.getField(5, 1);

        Field field30 = board.getField(3, 0);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        Stone stone7 = new Stone(StoneColor.BLUE, StoneShape.BELL);

        field01.setStone(stone);
        field11.setStone(stone2);
        field21.setStone(stone3);
        field41.setStone(stone4);
        field51.setStone(stone6);

        field30.setStone(stone7);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone5, field31);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(14, pointsForMove);
    }

    @Test
    public void testGetPointsComplexMove5() throws Exception {
        Board board = new Board();
        Field field01 = board.getField(0, 1);
        Field field11 = board.getField(1, 1);
        Field field21 = board.getField(2, 1);
        Field field31 = board.getField(3, 1);
        Field field41 = board.getField(4, 1);
        Field field51 = board.getField(5, 1);

        Field field30 = board.getField(3, 0);
        Field field32 = board.getField(3, 2);
        Field field33 = board.getField(3, 3);
        Field field34 = board.getField(3, 4);
        Field field35 = board.getField(3, 5);

        Stone stone = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone2 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone3 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone4 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone5 = new Stone(StoneColor.BLUE, StoneShape.HEART);
        Stone stone6 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        Stone stone7 = new Stone(StoneColor.BLUE, StoneShape.ACORN);
        Stone stone8 = new Stone(StoneColor.BLUE, StoneShape.BELL);
        Stone stone9 = new Stone(StoneColor.BLUE, StoneShape.CLUBS);
        Stone stone10 = new Stone(StoneColor.BLUE, StoneShape.DIAMONT);
        Stone stone11 = new Stone(StoneColor.BLUE, StoneShape.SPADES);

        field01.setStone(stone);
        field11.setStone(stone2);
        field21.setStone(stone3);
        field41.setStone(stone4);
        field51.setStone(stone6);

        field30.setStone(stone7);
        field33.setStone(stone9);
        field34.setStone(stone10);
        field35.setStone(stone11);

        HashMap<Stone, Field> stoneToFieldMap = new HashMap<Stone, Field>();

        stoneToFieldMap.put(stone5, field31);
        stoneToFieldMap.put(stone8, field32);

        int pointsForMove = PointsCalculator.getPointsForMove(stoneToFieldMap,
                board);
        assertEquals(24, pointsForMove);
    }
}
