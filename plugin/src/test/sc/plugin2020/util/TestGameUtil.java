package sc.plugin2020.util;

import org.junit.Assert;
import sc.plugin2020.Board;
import sc.plugin2020.FieldState;

public class TestGameUtil {

  public static Board createCustomBoard(String boardString) {
    Assert.assertEquals("Length of boardString does not match size of the Board",
            Constants.BOARD_SIZE * Constants.BOARD_SIZE, boardString.length());

    Board board = new Board();
    for(int i = 0; i < Constants.BOARD_SIZE; i++) {
      for(int j = 0; j < Constants.BOARD_SIZE; j++) {
        int index = i + j * Constants.BOARD_SIZE;
        char c = boardString.charAt(index);
        if(c == 'R') {
          board.getField(i, j).setState(FieldState.RED);
        } else if(c == 'B') {
          board.getField(i, j).setState(FieldState.BLUE);
        } else if(c == 'O') {
          board.getField(i, j).setState(FieldState.OBSTRUCTED);
        } else {
          board.getField(i, j).setState(FieldState.EMPTY);
        }
      }
    }
    return board;
  }

}

