package sc.plugin2020;

import org.junit.Before;
import org.junit.Test;
import sc.plugin2020.util.GameRuleLogic;
import sc.plugin2020.util.TestGameUtil;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.security.InvalidParameterException;

import static org.junit.Assert.*;
import static sc.plugin2020.util.TestJUnitUtil.assertThrows;

public class GamePlayTest {

  private Game game;
  private GameState state;

  @Before
  public void beforeEveryTest() {
    game = new Game();
    state = game.getGameState();
    state.setCurrentPlayerColor(PlayerColor.RED);
  }

  @Test
  public void boardCreationTest() {
    Board board = new Board();
    assertNotNull(board.getField(0,0,0));
  }

  @Test
  public void invalidBoardStringTest() {

    assertThrows(InvalidParameterException.class, () ->
            TestGameUtil.createCustomBoard("" +
                    "XY--------" +
                   "------------" +
                  "--------------" +
                 "----------------" +
                "------------------" +
                 "----------------" +
                  "--------------" +
                   "------------" +
                    "----------" )
    );
    assertThrows(InvalidParameterException.class, () ->
            TestGameUtil.createCustomBoard("" +
                    "BY--------" +
                   "------------" +
                  "--------------" +
                 "----------------" +
                "------------------" +
                 "----------------" +
                  "--------------" +
                   "------------" +
                    "----------" )
    );
  }

  @Test
  public void onlyEndAfterRoundTest() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
                "RB--------" +
               "------------" +
              "--------------" +
             "----------------" +
            "------------------" +
             "----------------" +
              "--------------" +
               "------------" +
                "----------" );
    state.setBoard(board);
    {
      //Move move = new Move();
      //GameRuleLogic.performMove(state, move);
      assertEquals(1, GameRuleLogic.findPiecesOfTypeAndPlayer(state.getBoard(), PieceType.BEETLE, PlayerColor.RED).size());
    }
  }

  @Test
  public void gameEndTest() {
    Board board = TestGameUtil.createCustomBoard("" +
                "----------" +
               "------------" +
              "--------------" +
             "----BBBB--------" +
            "----BBRQBB--------" +
             "----BBBB--------" +
              "--------------" +
               "------------" +
                "----------" );
    // TODO: This test currently fails. Check if getneighbours works correctly (write another test for that)
    assertTrue(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED));
  }
}
