package sc.plugin2016.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2016.Board;
import sc.plugin2016.Connection;
import sc.plugin2016.Field;
import sc.plugin2016.GameState;
import sc.plugin2016.Move;
import sc.plugin2016.Player;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.util.Constants;
import sc.plugin2016.util.InvalidMoveException;

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
    /*for (Connection c : board.connections) {
      System.out.println("x1 = " + c.x1 + ", y1 = " + c.y1 + ", x2 = " + c.x2 + ", y2 = " + c.y2);
    }*/
    assertEquals(0, board.getConnections(5, 7).size());
    assertEquals(0, board.getConnections(6, 5).size());
    assertEquals(2, board.connections.size());
    
    

    board.put(10, 19, redPlayer);
    board.put(6, 19, redPlayer);
    board.put(8, 18, redPlayer);
    assertEquals(2, board.getConnections(8, 18).size());
    assertEquals(1, board.getConnections(6, 19).size());
    assertEquals(4, board.connections.size());
  }
  
  @Test(expected = InvalidMoveException.class)
  public void testPuttingPlayerOnEnemyBase() throws InvalidMoveException {
    GameState state = new GameState();
    state.addPlayer(new Player(PlayerColor.RED));
    state.addPlayer(new Player(PlayerColor.BLUE));
    Player currentPlayer = state.getCurrentPlayer();
    Move move = new Move(0, 1);
    assertEquals(PlayerColor.RED, currentPlayer.getPlayerColor());
    move.perform(state, currentPlayer);
  }
  
  @Test
  public void testPuttingPlayerOnOwnBase() throws InvalidMoveException {
    GameState state = new GameState();
    PlayerColor red = PlayerColor.RED;
    PlayerColor blue = PlayerColor.BLUE;
    state.addPlayer(new Player(red));
    state.addPlayer(new Player(blue));
    Player currentPlayer = state.getCurrentPlayer();
    Move move = new Move(1, 0);
    assertEquals(red, currentPlayer.getPlayerColor());
    move.perform(state, currentPlayer);
  }
  
  @Test
  public void testGetConnections() throws InvalidMoveException {
    Board board = new Board();
    Player red = new Player(PlayerColor.RED);
    board.put(5, 5, red);
    board.put(6, 7, red);
    board.put(4, 8, red);
    assertTrue(board.getConnections(6, 7).contains(new Connection(6, 7, 5, 5, red.getPlayerColor())));
    assertTrue(board.getConnections(6, 7).contains(new Connection(6, 7, 4, 8, red.getPlayerColor())));
  }
}
