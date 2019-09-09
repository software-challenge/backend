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
        assertThrows(InvalidParameterException::class.java) {
            TestGameUtil.updateGamestateWithBoard(state, "" +
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
        assertThrows(InvalidParameterException::class.java) {
            TestGameUtil.updateGamestateWithBoard(state, "" +
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

    @Ignore
    @Test
    @Throws(InvalidMoveException::class)
    fun onlyEndAfterRoundTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "    RB--------" +
                "   ------------" +
                "  --------------" +
                " ----------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        run {
            //Move move = new Move();
            //GameRuleLogic.performMove(state, move);
        }
    }

    @Test
    fun getNeighbourTest() {
        val n = GameRuleLogic.getNeighbours(Board(), CubeCoordinates(-2, 1))
        val expected = arrayOf(CubeCoordinates(-2, 2), CubeCoordinates(-1, 1), CubeCoordinates(-1, 0), CubeCoordinates(-2, 0), CubeCoordinates(-3, 1), CubeCoordinates(-3, 2))
        assertArrayEquals(
                Arrays.stream(expected).sorted().toArray(),
                n.stream().map { f: Field -> f.coordinates }.sorted().toArray()
        )
    }

    @Test
    fun gameEndTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----BBBB--------" +
                    "----BBRQBB--------" +
                    " ----BBBB--------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertTrue(GameRuleLogic.isQueenBlocked(state.board, PlayerColor.RED))
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----BBBB--------" +
                    "------RQBB--------" +
                    " ----BBBB--------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertFalse(GameRuleLogic.isQueenBlocked(state.board, PlayerColor.RED))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveOnEmptyBoardTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveOutsideOfBoard() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(8, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun validSetMoveTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " --BG------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveOfUnavailablePieceTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGBG------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.getUndeployedPieces(PlayerColor.RED).clear()
            val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(-4, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun setMoveConnectionToSwarmTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGBG------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
            val move2 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-4, 4))
            assertTrue(GameRuleLogic.validateMove(state, move2))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveNonexistentPieceTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveOfSolePieceOnBoardTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RQ--------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveOntoOtherPieceTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RQBQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, -1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveBeforeQueenTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RBBG------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveAfterQueenTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RBBG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveBeeValidTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RBBG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, -1), CubeCoordinates(1, -2))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveBeeTooFarTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RBBG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, -1), CubeCoordinates(2, -2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveBeetleTooFarTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RBBG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(2, -1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveBlockedBeetleTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------BA--------" +
                    "--------RBBG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----BGBA--------" +
                    "------RBRBBG------" +
                    " ------RARQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveGrasshopperValidTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------BABB------" +
                    "------BQRBRG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(1, -1), CubeCoordinates(-2, 2))
            assertTrue(GameRuleLogic.validateMove(state, move))
            val move2 = DragMove(CubeCoordinates(1, -1), CubeCoordinates(1, 1))
            assertTrue(GameRuleLogic.validateMove(state, move2))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveGrasshopperOverEmptyFieldTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------BABB------" +
                    "------BQRBRG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(1, -1), CubeCoordinates(-3, 3))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveGrasshopperToNeighbourTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------BABB------" +
                    "------BQRBRG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(1, -1), CubeCoordinates(1, -2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveAntValidTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------RABB------" +
                    "------BQRBRG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
            assertTrue(GameRuleLogic.validateMove(state, move))
            val move2 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, -2))
            assertTrue(GameRuleLogic.validateMove(state, move2))
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveAntIntoBlockedTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------RB----" +
                    " ------RABB--BB--" +
                    "------BQRBRGBB----" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, -1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveAntDisconnectFromSwarmTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------RABB------" +
                    "------BQRBRG------" +
                    " --------RQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, 1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }

    @Test
    @Throws(InvalidMoveException::class)
    fun dragMoveSpiderTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------BB----" +
                    " ------RS--BS----" +
                    "------RBRQBQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val validOne = DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, 1))
            assertTrue(GameRuleLogic.validateMove(state, validOne))
            val validTwo = DragMove(CubeCoordinates(0, 1), CubeCoordinates(3, 0))
            assertTrue(GameRuleLogic.validateMove(state, validTwo))
            val validThree = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-2, 1))
            assertTrue(GameRuleLogic.validateMove(state, validThree))
            val invalidOne = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalidOne) }
            val invalidTwo = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalidTwo) }
            val invalidThree = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalidThree) }
            val invalidFour = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalidFour) }
        }
    }

    @Test
    @Ignore
    // use to see the generated XML
    fun gamestateToXmlTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "    ----------" +
                "   ------------" +
                "  --------------" +
                " --BG------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        state.board.getField(0, 0).pieces.add(Piece(PlayerColor.RED, PieceType.ANT))
        state.board.getField(0, 0).pieces.add(Piece(PlayerColor.BLUE, PieceType.BEE))
        TestGameUtil.updateUndeployedPiecesFromBoard(state)
        assertEquals(PieceType.BEE, state.board.getField(0, 0).pieces.lastElement().type)
        assertEquals(PieceType.BEE, state.board.getField(0, 0).pieces.peek().type)
        val xstream = Configuration.xStream
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
    fun gamestateFromXmlTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "    ----------" +
                "   ------------" +
                "  --------------" +
                " --BG------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        state.board.getField(0, 0).pieces.add(Piece(PlayerColor.RED, PieceType.ANT))
        state.board.getField(0, 0).pieces.add(Piece(PlayerColor.BLUE, PieceType.BEE))
        TestGameUtil.updateUndeployedPiecesFromBoard(state);
        assertEquals(PieceType.BEE, state.board.getField(0, 0).pieces.lastElement().type)
        assertEquals(PieceType.BEE, state.board.getField(0, 0).pieces.peek().type)
        val xstream = Configuration.xStream
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
        val xstream = Configuration.xStream
        val xml = xstream.toXML(RoomPacket(roomId, move))
        val expect =
                "<room roomId=\"" + roomId + "\">\n" +
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
    fun xmlToDragMoveTest() {
        val xstream = Configuration.xStream
        val xml =
                """<room roomId="42">
              <data class="dragmove">
                <start>
                  <x>0</x>
                  <y>-1</y>
                  <z>1</z>
                </start>
                <destination>
                  <x>1</x>
                  <y>2</y>
                  <z>-3</z>
                </destination>
              </data>
            </room>"""
        val room = xstream.fromXML(xml) as RoomPacket
        val expect = DragMove(CubeCoordinates(0, -1, 1), CubeCoordinates(1, 2, -3))
        assertEquals(expect, room.data)
    }

    @Test
    fun xmlToSetMoveTest() {
        val xstream = Configuration.xStream
        val xml =
                "<room roomId=\"64a0482c-f368-4e33-9684-d5106228bb75\">" +
                        "  <data class=\"setmove\">" +
                        "    <piece owner=\"RED\" type=\"BEETLE\" />" +
                        "    <destination><x>-2</x><y>0</y><z>2</z></destination>" +
                        "  </data>" +
                        "</room>"
        val room = xstream.fromXML(xml) as RoomPacket
        val expect = SetMove(Piece(PlayerColor.RED, PieceType.BEETLE), CubeCoordinates(-2, 0, 2))
        assertEquals(expect, room.data)
    }

    @Test
    fun performMoveTest() {
        val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        GameRuleLogic.performMove(state, move)
        assertEquals(PieceType.ANT, state.board.getField(1, 2, -3).pieces.lastElement().type)
    }
}
