package sc.plugin2020.util;

import org.junit.Assert;
import sc.plugin2020.Board;
import sc.plugin2020.FieldState;

public class TestGameUtil {

  public static Board createCustomBoard(String boardString) {
    Assert.assertEquals("Length of boardString does not match size of the Board",
            Constants.FIELD_AMOUNT*2, boardString.length());

    Board board = new Board();

    return board;
  }

}

