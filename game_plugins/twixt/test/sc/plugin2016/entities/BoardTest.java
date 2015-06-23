package sc.plugin2016.entities;

import static org.junit.Assert.*;

import java.awt.geom.Line2D;

import org.junit.Test;

import sc.plugin2016.Board;
import sc.plugin2016.Field;
import sc.plugin2016.FieldType;
import sc.plugin2016.Player;
import sc.plugin2016.PlayerColor;

public class BoardTest {
  
  
  @Test
  public void testWires() {
    Board testBoard = new Board();
    Field field1 = new Field(FieldType.NORMAL,5,5);
    Field field2 = new Field(FieldType.NORMAL,7,6);
    testBoard.connections.put(field1, field2, PlayerColor.BLUE);
    System.out.println(testBoard.connections.column(field1).toString());
    System.out.println(testBoard.connections.row(field1).toString());
    System.out.println(testBoard.connections.column(field2).toString());
    System.out.println(testBoard.connections.row(field2).toString());
    System.out.println(testBoard.connections.toString());
    System.out.println(testBoard.connections.size());
    
    
    
    Board board = new Board();
    Player redPlayer = new Player(PlayerColor.RED);
    Player bluePlayer = new Player(PlayerColor.BLUE);
    board.put(5, 5, redPlayer);
    board.put(6, 7, redPlayer);
//    assertEquals(board.getField(5, 5).getConnections().size(), 1);
//    assertEquals(board.getField(5, 5).getConnections().get(0), board.getField(6, 7));
    assertEquals(1, board.connections.size());
    assertEquals(1, board.connections.row(board.getField(5,5)).size() + board.connections.column(board.getField(5,5)).size());
    assertTrue(board.connections.row(board.getField(5,5)).containsKey(board.getField(6,7)) || 
               board.connections.column(board.getField(5,5)).containsKey(board.getField(6,7)));
    
    board.put(5, 6, redPlayer);
    board.put(6, 8, redPlayer);
//    assertEquals(board.getField(5, 5).getConnections().size(), 1);
//    assertEquals(board.getField(5, 6).getConnections().size(), 1);
//    assertEquals(board.getField(5, 6).getConnections().get(0), board.getField(6, 8));
    assertEquals(2, board.connections.size());
    assertEquals(1, board.connections.row(board.getField(5,5)).size() + board.connections.column(board.getField(5,5)).size());
    assertEquals(1, board.connections.row(board.getField(5,6)).size() + board.connections.column(board.getField(5,6)).size());
    assertTrue(board.connections.row(board.getField(5,6)).containsKey(board.getField(6,8)) || 
        board.connections.column(board.getField(5,6)).containsKey(board.getField(6,8)));
    board.put(5, 7, bluePlayer);
    board.put(6, 5, bluePlayer);
//    assertEquals(board.getField(5, 7).getConnections().size(), 0); 
//    assertEquals(board.getField(6, 5).getConnections().size(), 0);  
    assertEquals(2, board.connections.size());  // Hier ist noch ein Problem irgendwo
    
  }
  
  
/*
  @Test(expected = IllegalArgumentException.class)
  public void testPutPenguinOutOfBoundsLowY() {
    Board board = new Board();
    board.putPenguin(0, -1, new Penguin(PlayerColor.RED));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutPenguinOutOfBoundsHighY() {
    Board board = new Board();
    board.putPenguin(0, 8, new Penguin(PlayerColor.RED));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutPenguinOutOfBoundsLowX() {
    Board board = new Board();
    board.putPenguin(-1, 0, new Penguin(PlayerColor.RED));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutPenguinOutOfBoundsHighX() {
    Board board = new Board();
    board.putPenguin(8, 0, new Penguin(PlayerColor.RED));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutPenguinOnEmptyField() {
    Board board = new Board(false);
    board.putPenguin(1, 1, new Penguin(PlayerColor.RED));
  }

  @Test
  public void testPutPenguin() {
    Board board = new Board();
    board.getField(0, 0).fish = 1;
    Penguin penguin = new Penguin(PlayerColor.RED);
    assertNull(board.getField(0, 0).getPenguin());

    board.putPenguin(0, 0, penguin);
    assertEquals(penguin, board.getField(0, 0).getPenguin());
  }

  @Test
  public void testClone() {
    Board board = new Board();
    board.getField(0, 0).fish = 1;
    Penguin penguin = new Penguin();
    board.putPenguin(0, 0, penguin);
    Board clone;
    try {
      clone = (Board) board.clone();
      assertTrue(board.equals(clone));
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    
  }*/

}
