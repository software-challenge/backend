package sc.plugin2016.entities;

import static org.junit.Assert.*;


import org.junit.Test;

import sc.plugin2016.Board;
import sc.plugin2016.Connection;
import sc.plugin2016.Player;
import sc.plugin2016.PlayerColor;

public class BoardTest {
  
  
  @Test
  public void testWires() {
    Board board = new Board();
    Player redPlayer = new Player(PlayerColor.RED);
    Player bluePlayer = new Player(PlayerColor.BLUE);
    board.put(5, 5, redPlayer);
    board.put(6, 7, redPlayer);
    assertEquals(1, board.connections.size());
    assertTrue(board.connections.contains(new Connection(5, 5, 6, 7, PlayerColor.RED)));
    
    board.put(5, 6, redPlayer);
    board.put(6, 8, redPlayer);
    assertEquals(2, board.connections.size());
    assertEquals(1, board.getConnections(5, 5).size());
    assertEquals(1, board.getConnections(5, 6).size());
    assertTrue(board.connections.contains(new Connection(5, 6, 6, 8, PlayerColor.RED)));
    board.put(5, 7, bluePlayer);
    board.put(6, 5, bluePlayer); 
    for (Connection c : board.connections) {
      System.out.println("x1 = " + c.x1 + ", y1 = " + c.y1 + ", x2 = " + c.x2 + ", y2 = " + c.y2);
    }
    assertEquals(0, board.getConnections(5, 7).size());
    assertEquals(0, board.getConnections(6, 5).size());
    assertEquals(2, board.connections.size());
    
  }
}
