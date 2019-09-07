package sc.plugin2020

import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import sc.plugin2020.util.Configuration
import sc.plugin2020.util.CubeCoordinates
import sc.plugin2020.util.GameRuleLogic
import sc.plugin2020.util.TestGameUtil
import sc.plugin2020.util.TestJUnitUtil.assertThrows
import sc.protocol.responses.RoomPacket
import sc.shared.InvalidMoveException
import sc.shared.PlayerColor
import java.security.InvalidParameterException
import java.util.*

class GamePlayTest {

    private lateinit var game: Game
    private lateinit var state: GameState

    @Before
    fun beforeEveryTest() {
        game = Game()
        state = game.gameState
        state.currentPlayerColor = PlayerColor.RED
    }

    @Test
    fun boardCreationTest() {
        val board = Board()
        assertNotNull(board.getField(0, 0, 0))
    }

    @Test
    fun invalidBoardStringTest() {
        assertThrows(InvalidParameterException::class.java
        ) {
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
        }
        assertThrows(InvalidParameterException::class.java
        ) {
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
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun onlyEndAfterRoundTest() {
        val board = TestGameUtil.createCustomBoard("" +
                "    RB--------" +
                "   ------------" +
                "  --------------" +
                " ----------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        state.setBoard(board)
        run {
            //Move move = new Move();
            //GameRuleLogic.performMove(state, move);
            assertEquals(1, GameRuleLogic.findPiecesOfTypeAndPlayer(state.board, PieceType.BEETLE, PlayerColor.RED).size.toLong())
        }
    }

    @Test
    fun getNeighbourTest() {
        val n = GameRuleLogic.getNeighbours(Board(), CubeCoordinates(-2, 1))
        val expected = arrayOf(CubeCoordinates(-2, 2), CubeCoordinates(-1, 1), CubeCoordinates(-1, 0), CubeCoordinates(-2, 0), CubeCoordinates(-3, 1), CubeCoordinates(-3, 2))
        assertArrayEquals(
                Arrays.stream(expected).sorted().toArray(),
                n.stream().map { f: Field -> f.position }.sorted().toArray()
        )
    }

    @Test
    fun gameEndTest() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----BBBB--------" +
                    "----BBRQBB--------" +
                    " ----BBBB--------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertTrue(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED))
        }
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----BBBB--------" +
                    "------RQBB--------" +
                    " ----BBBB--------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertFalse(GameRuleLogic.isQueenBlocked(board, PlayerColor.RED))
        }
    }

    @Test
    fun findFieldsOwnedByPlayerTest() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   RG----------" +
                    "  --BQ----------" +
                    " BGBB------------" +
                    "----RQ------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            assertEquals(3, GameRuleLogic.fieldsOwnedByPlayer(board, PlayerColor.BLUE).size.toLong())
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveTest() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveTest1() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(8, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveTest2() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " --BG------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveTest3() {
        run {
            state.getUndeployedPieces(PlayerColor.RED).clear()
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGBG------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(-4, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveTest4() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGBG------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
            val move2 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-4, 4))
            assertTrue(GameRuleLogic.validateMove(state, move2))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveTest5() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGRGRG----------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-4, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveTest() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveTest2() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RQ--------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveTest3() {
        run {
            val board = TestGameUtil.createCustomBoard("" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RQBQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.setBoard(board)
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, -1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Ignore
    // use to see the generated XML
    fun toXmlTest() {
        val board = TestGameUtil.createCustomBoard("" +
                "    ----------" +
                "   ------------" +
                "  --------------" +
                " --BG------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        state.setBoard(board)
        val pieces = arrayOf<Piece>()
        board.getField(0, 0).pieces.add(Piece(PlayerColor.RED, PieceType.ANT))
        board.getField(0, 0).pieces.add(Piece(PlayerColor.BLUE, PieceType.BEE))
        assertEquals(PieceType.BEE, board.getField(0, 0).pieces.lastElement().type)
        assertEquals(PieceType.BEE, board.getField(0, 0).pieces.peek().type)
        val xstream = Configuration.getXStream()
        val xml = xstream.toXML(state)
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
                "</state>", xml)
    }

    @Test
    @Ignore
    fun fromXmlTest() {
        val board = TestGameUtil.createCustomBoard("" +
                "    ----------" +
                "   ------------" +
                "  --------------" +
                " --BG------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        state.setBoard(board)
        val pieces = arrayOf<Piece>()
        board.getField(0, 0).pieces.add(Piece(PlayerColor.RED, PieceType.ANT))
        board.getField(0, 0).pieces.add(Piece(PlayerColor.BLUE, PieceType.BEE))
        assertEquals(PieceType.BEE, board.getField(0, 0).pieces.lastElement().type)
        assertEquals(PieceType.BEE, board.getField(0, 0).pieces.peek().type)
        val xstream = Configuration.getXStream()
        val xmlState = GameState()
        val read = xstream.fromXML(
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
                        "</state>", xmlState) as GameState
        assertEquals(state, read)
    }

    @Test
    fun moveToXmlTest() {
        val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        val roomId = "42"
        val xstream = Configuration.getXStream()
        val xml = xstream.toXML(RoomPacket(roomId, move))
        val expect = "<room roomId=\"" + roomId + "\">\n" +
                "  <data class=\"setmove\">\n" +
                "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
                "    <destination>\n" +
                "      <x>1</x>\n" +
                "      <y>2</y>\n" +
                "      <z>-3</z>\n" +
                "    </destination>\n" +
                "  </data>\n" +
                "</room>"
        assertEquals(expect, xml)
    }

    @Test
    fun xmlToMoveTest() {
        val xstream = Configuration.getXStream()
        val xml = "<room roomId=\"42\">\n" +
                "  <data class=\"setmove\">\n" +
                "    <destination>\n" +
                "      <x>1</x>\n" +
                "      <y>2</y>\n" +
                "      <z>-3</z>\n" +
                "    </destination>\n" +
                "    <piece owner=\"RED\" type=\"ANT\"/>\n" +
                "  </data>\n" +
                "</room>"
        val room = xstream.fromXML(xml) as RoomPacket
        val expect = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        assertEquals(expect, room.data)
    }

    @Test
    fun performMoveTest() {
        val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        GameRuleLogic.performMove(state, move)
        assertEquals(PieceType.ANT, state.board.getField(1, 2, -3).pieces.lastElement().type)
    }
}
