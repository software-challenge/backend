package sc.plugin2016.entities;

import static org.junit.Assert.*;

import java.awt.List;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;

import sc.plugin2016.FieldType;
import sc.plugin2016.GameState;
import sc.plugin2016.Move;
import sc.plugin2016.Player;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.util.Constants;
import sc.plugin2016.util.InvalidMoveException;

public class MoveTest {

  LinkedList<Move> allMovesRed = getAllMoves();

  LinkedList<Move> allMovesBlue = getAllMoves();

  GameState state = new GameState();
  
  Player red = new Player(PlayerColor.RED);
  Player blue = new Player(PlayerColor.BLUE);
  
  public void checkAllMoves() throws CloneNotSupportedException, InvalidMoveException {
    
    for(int x = 0; x < Constants.SIZE; x++) {
      for(int y = 0; y< Constants.SIZE; y++) {
        GameState clone = (GameState) state.clone();
        clone.addPlayer(red);
        clone.addPlayer(blue);
        Move move = new Move(x,y);
        if(clone.getBoard().getField(x, y).getType() == FieldType.SWAMP) {
          // moves should be tested
        } else if(clone.getBoard().getField(x, y).getType() == FieldType.RED) {
          // only test for RED
          allMovesBlue.remove(move);
        } else if(clone.getBoard().getField(x, y).getType() == FieldType.BLUE) {
         // only test for BLUE
          allMovesRed.remove(move);
        } else if(clone.getBoard().getField(x, y).getOwner() != null) {
          // moves should be tested
        } else {
          //TODO fix this
          if(clone.getBoard() == null) {
            System.out.println("board ist null");
          }
          if(clone.getBoard().getField(x, y) == null) {
            System.out.println("field ist null");
          }
          if(clone.getBoard().getField(x, y).getType() == null) {
            System.out.println("type ist null");
          }
          if(clone.getRedPlayer() == null) {
            System.out.println("red ist null");
          }
          move.perform(state,state.getRedPlayer());
        }
          
      }
    }
  }
  
  private LinkedList<Move> getAllMoves() {
    LinkedList<Move> allMoves = new LinkedList<Move>();
    for(int x = 0; x < Constants.SIZE; x++) {
      for(int y = 0; y < Constants.SIZE; y ++) {
        allMoves.add(new Move(x,y));
      }
    }
    return allMoves;
  }

  @Test(expected=InvalidMoveException.class)
  public void testAllMoves() throws CloneNotSupportedException, InvalidMoveException {
    state.addPlayer(red);
    state.addPlayer(blue);
    checkAllMoves();
    for(Move m : allMovesRed) {
      GameState clone = (GameState) state.clone();
      m.perform(clone, clone.getRedPlayer());
      assertTrue(false);
    }
    for(Move m : allMovesBlue) {
      GameState clone = (GameState) state.clone();
      m.perform(clone, clone.getBluePlayer());
      assertTrue(false);
    }
    
  }
}
