package sc.plugin2020.util;

import org.junit.Assert;
import sc.plugin2020.Board;
import sc.plugin2020.FieldState;

public class TestGameUtil {

  public static Board createCustomBoard(String boardString) {//Hardcoded auf Feldgröße von 9
    Assert.assertEquals("Length of boardString does not match size of the Board",
            Constants.FIELD_AMOUNT*2, boardString.length());

    int[][] fields = {              {0,4},{1,3},{2,2},{3,1},{4,0},
                                {-1,4},{0,3},{1,2},{2,1},{3,0},{4,-1},
                            {-2,4},{-1,3},{0,2},{1,1},{2,0},{3,-1},{4,-2},
                        {-3,4},{-2,3},{-1,2},{0,1},{1,0},{2,-1},{3,-2},{4,-3},
                    {-4,4},{-3,3},{-2,2},{-1,1},{0,0},{1,-1},{2,-2},{3,-3},{4,-4},
                        {-4,3},{-3,2},{-2,1},{-1,0},{0,-1},{1,-2},{2,-3},{3,-4},
                            {-4,2},{-3,1},{-2,0},{-1,-1},{0,-2},{1,-3},{2,-4},
                                {-4,1},{-3,0},{-2,-1},{-1,-2},{0,-3},{1,-4},
                                    {-4,0},{-3,-1},{-2,-2},{-1,-3},{0,-4}
    };
    Board board = new Board();

    for(int i = 0; i < fields.length; i++) {

    }
    return board;
  }

}

