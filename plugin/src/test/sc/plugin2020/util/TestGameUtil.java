package sc.plugin2020.util;

import org.junit.Assert;
import sc.plugin2020.Board;
import sc.plugin2020.FieldState;
import sc.plugin2020.Piece;
import sc.plugin2020.PieceType;
import sc.shared.PlayerColor;

public class TestGameUtil {

  private static Piece parsePiece (PlayerColor pc, char c){
    switch (c)
    {
      case 'Q':
        return new Piece(pc, PieceType.BEE);

      case 'B':
        return new Piece(pc, PieceType.BEETLE);

      case 'G':
        return new Piece(pc, PieceType.GRASSHOPPER);

      case 'S':
        return new Piece(pc, PieceType.SPIDER);

      case 'A':
        return new Piece(pc, PieceType.ANT);
    }
    return null;
  }

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
    char[] tmp = boardString.toCharArray();

    for(int i = 0; i < fields.length; i++) {
      board.getField(fields[i][0],fields[i][1]).getPieces().clear();
      switch (tmp[i*2-1])
      {
        case 'R':
          board.getField(fields[i][0],fields[i][1]).getPieces().add(parsePiece(PlayerColor.RED, tmp[i*2]));
          break;

        case 'B':
          board.getField(fields[i][0],fields[i][1]).getPieces().add(parsePiece(PlayerColor.BLUE, tmp[i*2]));
          break;

        case 'O':
          board.getField(fields[i][0],fields[i][1]).setObstructed(true);
      }
    }
    return board;
  }
}

