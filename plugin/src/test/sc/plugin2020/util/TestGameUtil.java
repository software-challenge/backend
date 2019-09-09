package sc.plugin2020.util;

import org.junit.Assert;
import sc.plugin2020.Board;
import sc.plugin2020.GameState;
import sc.plugin2020.Piece;
import sc.plugin2020.PieceType;
import sc.shared.PlayerColor;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

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
      default:
        throw new InvalidParameterException("Expected piecetype character to be one of Q,B,G,S or A, was: "+c);

    }
  }

  public static Board createCustomBoard(String boardString) {//Hardcoded auf Feldgröße von 9
    String boardStringWithoutWhitespace = boardString.replaceAll(" ", "");
    Assert.assertEquals("Length of boardString does not match size of the Board",
            Constants.FIELD_AMOUNT *2, boardStringWithoutWhitespace.length());

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
    char[] tmp = boardStringWithoutWhitespace.toCharArray();

    for(int i = 0; i < fields.length; i++) {
      board.getField(fields[i][0],fields[i][1]).getPieces().clear();
      switch (tmp[i*2])
      {
        case 'R':
          board.getField(fields[i][0],fields[i][1]).getPieces().add(parsePiece(PlayerColor.RED, tmp[i*2+1]));
          break;

        case 'B':
          board.getField(fields[i][0],fields[i][1]).getPieces().add(parsePiece(PlayerColor.BLUE, tmp[i*2+1]));
          break;

        case 'O':
          board.getField(fields[i][0],fields[i][1]).setObstructed(true);
          break;
        case '-':
          // empty field
          break;
        default:
          throw new InvalidParameterException("Expected first character to be either B (blue), R (red) or O (obstructed), was: "+tmp[i*2]);
      }
    }
    return board;
  }

  public static void updateUndeployedPiecesFromBoard(GameState gs) {
    for(PlayerColor color: PlayerColor.values()) {
      List<Piece> p = gs.getDeployedPieces(color);
      gs.getUndeployedPieces(color).removeAll(p);
    }
  }

  public static void updateGamestateWithBoard(GameState gs, String customBoard) {
    Board board = TestGameUtil.createCustomBoard(customBoard);
    gs.setBoard(board);
    TestGameUtil.updateUndeployedPiecesFromBoard(gs);
  }

}

