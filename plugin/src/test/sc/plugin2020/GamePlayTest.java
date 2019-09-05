package sc.plugin2020;

import com.thoughtworks.xstream.XStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sc.plugin2020.util.Configuration;
import sc.plugin2020.util.CubeCoordinates;
import sc.plugin2020.util.GameRuleLogic;
import sc.plugin2020.util.TestGameUtil;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;

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
    assertNotNull(board.getField(0, 0, 0));
  }

  @Test
  public void invalidBoardStringTest() {
    assertThrows(InvalidParameterException.class, () ->
            TestGameUtil.createCustomBoard("" +
                    "    XY--------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
    );
    assertThrows(InvalidParameterException.class, () ->
            TestGameUtil.createCustomBoard("" +
                    "    BY--------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
    );
  }

  @Test
  public void onlyEndAfterRoundTest() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
            "    RB--------" +
            "   ------------" +
            "  --------------" +
            " ----------------" +
            "------------------" +
            " ----------------" +
            "  --------------" +
            "   ------------" +
            "    ----------");
    state.setBoard(board);
    {
      //Move move = new Move();
      //GameRuleLogic.performMove(state, move);
      assertEquals(1, GameRuleLogic.findPiecesOfTypeAndPlayer(state.getBoard(), PieceType.BEETLE, PlayerColor.RED).size());
    }
  }

  @Test
  public void getNeighbourTest() {
    ArrayList<Field> n = GameRuleLogic.getNeighbours(new Board(), new CubeCoordinates(-2, 1));
    CubeCoordinates[] expected = {
            new CubeCoordinates(-2, 2),
            new CubeCoordinates(-1, 1),
            new CubeCoordinates(-1, 0),
            new CubeCoordinates(-2, 0),
            new CubeCoordinates(-3, 1),
            new CubeCoordinates(-3, 2)
    };
    assertArrayEquals(
            Arrays.stream(expected).sorted().toArray(),
            n.stream().map((Field f) -> f.getPosition()).sorted().toArray()
    );
  }

  @Test
  public void gameEndTest() {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----BBBB--------" +
              "----BBRQBB--------" +
              " ----BBBB--------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      assertTrue(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED));
    }
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----BBBB--------" +
              "------RQBB--------" +
              " ----BBBB--------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      assertFalse(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED));
    }
  }

  @Test
  public void findFieldsOwnedByPlayerTest() {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   RG----------" +
              "  --BQ----------" +
              " BGBB------------" +
              "----RQ------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      assertEquals(3, GameRuleLogic.fieldsOwnedByPlayer(board, PlayerColor.BLUE).size());
    }
  }

  @Test
  public void setMoveTest() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(0, 0));
      assertTrue(GameRuleLogic.validateMove(state, move));
    }
  }

  @Test
  public void setMoveTest1() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(8, 0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
    }
  @Test
  public void setMoveTest2() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " --BG------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(0, 0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
  }
  @Test
  public void setMoveTest3() throws InvalidMoveException {
    {
      state.getUndeployedPieces(PlayerColor.RED).clear();
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " RGBG------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(new Piece(PlayerColor.RED, PieceType.ANT), new CubeCoordinates(-4, 4));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
  }
  @Test
  public void setMoveTest4() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " RGBG------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(0, 0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
      Move move2 = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(-4, 4));
      assertTrue(GameRuleLogic.validateMove(state, move2));
    }
  }
  @Test
  public void setMoveTest5() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " RGRGRG----------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(state.getUndeployedPieces(PlayerColor.RED).get(0), new CubeCoordinates(-4, 4));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
  }
  @Test
  public void drawMoveTest() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "------------------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(new CubeCoordinates(0, 0) , new CubeCoordinates(0, 0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
  }
  @Test
  public void drawMoveTest2() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "--------RQ--------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(new CubeCoordinates(0, 0) , new CubeCoordinates(0, 0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
  }
  @Test
  public void drawMoveTest3() throws InvalidMoveException {
    {
      Board board = TestGameUtil.createCustomBoard("" +
              "    ----------" +
              "   ------------" +
              "  --------------" +
              " ----------------" +
              "--------RQBQ------" +
              " ----------------" +
              "  --------------" +
              "   ------------" +
              "    ----------");
      state.setBoard(board);
      Move move = new Move(new CubeCoordinates(0, 0) , new CubeCoordinates(0, 0));
      assertThrows(InvalidMoveException.class, () -> GameRuleLogic.validateMove(state, move));
    }
  }
  @Test
  @Ignore
  // use to see the generated XML
  public void toXmlTest() {
    Board board = TestGameUtil.createCustomBoard("" +
            "    ----------" +
            "   ------------" +
            "  --------------" +
            " --BG------------" +
            "------------------" +
            " ----------------" +
            "  --------------" +
            "   ------------" +
            "    ----------");
    state.setBoard(board);
    Piece[] pieces = new Piece[]{,};
    board.getField(0, 0).getPieces().add(new Piece(PlayerColor.RED, PieceType.ANT));
    board.getField(0, 0).getPieces().add(new Piece(PlayerColor.BLUE, PieceType.BEE));
    assertEquals(PieceType.BEE, board.getField(0, 0).getPieces().lastElement().getPieceType());
    assertEquals(PieceType.BEE, board.getField(0, 0).getPieces().peek().getPieceType());
    XStream xstream = Configuration.getXStream();
    String xml = xstream.toXML(state);
    assertEquals("<state startPlayerColor=\"RED\" currentPlayerColor=\"RED\" turn=\"0\">\n" +
            "  <board>\n" +
            "    <fields>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <field obstructed=\"false\" x=\"-4\" y=\"0\" z=\"4\"/>\n" +
            "      <field obstructed=\"false\" x=\"-4\" y=\"1\" z=\"3\"/>\n" +
            "      <field obstructed=\"false\" x=\"-4\" y=\"2\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"-4\" y=\"3\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"-4\" y=\"4\" z=\"0\"/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <field obstructed=\"false\" x=\"-3\" y=\"-1\" z=\"4\"/>\n" +
            "      <field obstructed=\"false\" x=\"-3\" y=\"0\" z=\"3\"/>\n" +
            "      <field obstructed=\"false\" x=\"-3\" y=\"1\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"-3\" y=\"2\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"-3\" y=\"3\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"-3\" y=\"4\" z=\"-1\"/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"-2\" z=\"4\"/>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"-1\" z=\"3\"/>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"0\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"1\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"2\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"3\" z=\"-1\">\n" +
            "        <piece owner=\"BLUE\" type=\"GRASSHOPPER\"/>\n" +
            "      </field>\n" +
            "      <field obstructed=\"false\" x=\"-2\" y=\"4\" z=\"-2\"/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <null/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"-3\" z=\"4\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"-2\" z=\"3\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"-1\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"0\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"1\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"2\" z=\"-1\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"3\" z=\"-2\"/>\n" +
            "      <field obstructed=\"false\" x=\"-1\" y=\"4\" z=\"-3\"/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"-4\" z=\"4\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"-3\" z=\"3\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"-2\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"-1\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"0\" z=\"0\">\n" +
            "        <piece owner=\"RED\" type=\"ANT\"/>\n" +
            "        <piece owner=\"BLUE\" type=\"BEE\"/>\n" +
            "      </field>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"1\" z=\"-1\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"2\" z=\"-2\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"3\" z=\"-3\"/>\n" +
            "      <field obstructed=\"false\" x=\"0\" y=\"4\" z=\"-4\"/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"-4\" z=\"3\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"-3\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"-2\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"-1\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"0\" z=\"-1\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"1\" z=\"-2\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"2\" z=\"-3\"/>\n" +
            "      <field obstructed=\"false\" x=\"1\" y=\"3\" z=\"-4\"/>\n" +
            "      <null/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"-4\" z=\"2\"/>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"-3\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"-2\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"-1\" z=\"-1\"/>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"0\" z=\"-2\"/>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"1\" z=\"-3\"/>\n" +
            "      <field obstructed=\"false\" x=\"2\" y=\"2\" z=\"-4\"/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <field obstructed=\"false\" x=\"3\" y=\"-4\" z=\"1\"/>\n" +
            "      <field obstructed=\"false\" x=\"3\" y=\"-3\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"3\" y=\"-2\" z=\"-1\"/>\n" +
            "      <field obstructed=\"false\" x=\"3\" y=\"-1\" z=\"-2\"/>\n" +
            "      <field obstructed=\"false\" x=\"3\" y=\"0\" z=\"-3\"/>\n" +
            "      <field obstructed=\"false\" x=\"3\" y=\"1\" z=\"-4\"/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "    </fields>\n" +
            "    <fields>\n" +
            "      <field obstructed=\"false\" x=\"4\" y=\"-4\" z=\"0\"/>\n" +
            "      <field obstructed=\"false\" x=\"4\" y=\"-3\" z=\"-1\"/>\n" +
            "      <field obstructed=\"false\" x=\"4\" y=\"-2\" z=\"-2\"/>\n" +
            "      <field obstructed=\"false\" x=\"4\" y=\"-1\" z=\"-3\"/>\n" +
            "      <field obstructed=\"false\" x=\"4\" y=\"0\" z=\"-4\"/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "      <null/>\n" +
            "    </fields>\n" +
            "  </board>\n" +
            "  <undeployedRedPieces>\n" +
            "    <piece owner=\"RED\" type=\"BEE\"/>\n" +
            "    <piece owner=\"RED\" type=\"SPIDER\"/>\n" +
            "    <piece owner=\"RED\" type=\"SPIDER\"/>\n" +
            "    <piece owner=\"RED\" type=\"SPIDER\"/>\n" +
            "    <piece owner=\"RED\" type=\"GRASSHOPPER\"/>\n" +
            "    <piece owner=\"RED\" type=\"GRASSHOPPER\"/>\n" +
            "    <piece owner=\"RED\" type=\"BEETLE\"/>\n" +
            "    <piece owner=\"RED\" type=\"BEETLE\"/>\n" +
            "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
            "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
            "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
            "  </undeployedRedPieces>\n" +
            "  <undeployedBluePieces>\n" +
            "    <piece owner=\"BLUE\" type=\"BEE\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"SPIDER\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"SPIDER\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"SPIDER\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"GRASSHOPPER\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"GRASSHOPPER\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"BEETLE\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"BEETLE\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"ANT\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"ANT\"/>\n" +
            "    <piece owner=\"BLUE\" type=\"ANT\"/>\n" +
            "  </undeployedBluePieces>\n" +
            "</state>", xml);
  }

  @Test
  @Ignore
  public void fromXmlTest() {
    Board board = TestGameUtil.createCustomBoard("" +
            "    ----------" +
            "   ------------" +
            "  --------------" +
            " --BG------------" +
            "------------------" +
            " ----------------" +
            "  --------------" +
            "   ------------" +
            "    ----------");
    state.setBoard(board);
    Piece[] pieces = new Piece[]{,};
    board.getField(0, 0).getPieces().add(new Piece(PlayerColor.RED, PieceType.ANT));
    board.getField(0, 0).getPieces().add(new Piece(PlayerColor.BLUE, PieceType.BEE));
    assertEquals(PieceType.BEE, board.getField(0, 0).getPieces().lastElement().getPieceType());
    assertEquals(PieceType.BEE, board.getField(0, 0).getPieces().peek().getPieceType());
    XStream xstream = Configuration.getXStream();
    GameState xmlState = new GameState();
    GameState read = (GameState) xstream.fromXML(
            "<state startPlayerColor=\"RED\" currentPlayerColor=\"RED\" turn=\"0\">\n" +
                    "  <board>\n" +
                    "    <fields>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <field obstructed=\"false\" x=\"-4\" y=\"0\" z=\"4\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-4\" y=\"1\" z=\"3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-4\" y=\"2\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-4\" y=\"3\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-4\" y=\"4\" z=\"0\"/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <field obstructed=\"false\" x=\"-3\" y=\"-1\" z=\"4\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-3\" y=\"0\" z=\"3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-3\" y=\"1\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-3\" y=\"2\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-3\" y=\"3\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-3\" y=\"4\" z=\"-1\"/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"-2\" z=\"4\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"-1\" z=\"3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"0\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"1\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"2\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"3\" z=\"-1\">\n" +
                    "        <piece owner=\"BLUE\" type=\"GRASSHOPPER\"/>\n" +
                    "      </field>\n" +
                    "      <field obstructed=\"false\" x=\"-2\" y=\"4\" z=\"-2\"/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <null/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"-3\" z=\"4\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"-2\" z=\"3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"-1\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"0\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"1\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"2\" z=\"-1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"3\" z=\"-2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"-1\" y=\"4\" z=\"-3\"/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"-4\" z=\"4\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"-3\" z=\"3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"-2\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"-1\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"0\" z=\"0\">\n" +
                    "        <piece owner=\"RED\" type=\"ANT\"/>\n" +
                    "        <piece owner=\"BLUE\" type=\"BEE\"/>\n" +
                    "      </field>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"1\" z=\"-1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"2\" z=\"-2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"3\" z=\"-3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"0\" y=\"4\" z=\"-4\"/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"-4\" z=\"3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"-3\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"-2\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"-1\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"0\" z=\"-1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"1\" z=\"-2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"2\" z=\"-3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"1\" y=\"3\" z=\"-4\"/>\n" +
                    "      <null/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"-4\" z=\"2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"-3\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"-2\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"-1\" z=\"-1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"0\" z=\"-2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"1\" z=\"-3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"2\" y=\"2\" z=\"-4\"/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <field obstructed=\"false\" x=\"3\" y=\"-4\" z=\"1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"3\" y=\"-3\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"3\" y=\"-2\" z=\"-1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"3\" y=\"-1\" z=\"-2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"3\" y=\"0\" z=\"-3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"3\" y=\"1\" z=\"-4\"/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "    </fields>\n" +
                    "    <fields>\n" +
                    "      <field obstructed=\"false\" x=\"4\" y=\"-4\" z=\"0\"/>\n" +
                    "      <field obstructed=\"false\" x=\"4\" y=\"-3\" z=\"-1\"/>\n" +
                    "      <field obstructed=\"false\" x=\"4\" y=\"-2\" z=\"-2\"/>\n" +
                    "      <field obstructed=\"false\" x=\"4\" y=\"-1\" z=\"-3\"/>\n" +
                    "      <field obstructed=\"false\" x=\"4\" y=\"0\" z=\"-4\"/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "      <null/>\n" +
                    "    </fields>\n" +
                    "  </board>\n" +
                    "  <undeployedRedPieces>\n" +
                    "    <piece owner=\"RED\" type=\"BEE\"/>\n" +
                    "    <piece owner=\"RED\" type=\"SPIDER\"/>\n" +
                    "    <piece owner=\"RED\" type=\"SPIDER\"/>\n" +
                    "    <piece owner=\"RED\" type=\"SPIDER\"/>\n" +
                    "    <piece owner=\"RED\" type=\"GRASSHOPPER\"/>\n" +
                    "    <piece owner=\"RED\" type=\"GRASSHOPPER\"/>\n" +
                    "    <piece owner=\"RED\" type=\"BEETLE\"/>\n" +
                    "    <piece owner=\"RED\" type=\"BEETLE\"/>\n" +
                    "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
                    "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
                    "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
                    "  </undeployedRedPieces>\n" +
                    "  <undeployedBluePieces>\n" +
                    "    <piece owner=\"BLUE\" type=\"BEE\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"SPIDER\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"SPIDER\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"SPIDER\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"GRASSHOPPER\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"GRASSHOPPER\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"BEETLE\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"BEETLE\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"ANT\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"ANT\"/>\n" +
                    "    <piece owner=\"BLUE\" type=\"ANT\"/>\n" +
                    "  </undeployedBluePieces>\n" +
                    "</state>", xmlState);
    assertEquals(state, read);
  }
}
