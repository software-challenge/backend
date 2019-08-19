package sc.plugin2020;

import org.junit.Before;
import org.junit.Test;
import sc.plugin2020.util.Constants;
import sc.plugin2020.util.GameRuleLogic;
import sc.plugin2020.util.TestGameUtil;
import sc.plugin2020.util.Direction;
import sc.plugin2020.Move;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;
import sc.shared.WinCondition;
import sc.shared.WinReason;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static sc.plugin2020.FieldState.EMPTY;
import static sc.plugin2020.FieldState.RED;
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
  public void onlyEndAfterRoundTest() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
                "BB--------" +
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
      assertEquals(1, GameRuleLogic.findPieces(state.getBoard(), PlayerColor.BLUE, PieceType.BEETLE).size());
    }
  }
}
