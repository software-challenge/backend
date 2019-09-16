package sc.plugin2020

import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import sc.plugin2020.util.*
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
        assertEquals(board, board.clone())
    }
    
    @Test
    fun obstructedCreationTest() {
        val board = Board()
        assertEquals(3, board.fields.filter { it.isObstructed }.size)
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
    
    @Test
    fun boardCloneTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  ----BBOOBB----" +
                    " ----RBRS--BS----" +
                    "------RBRQBQ------" +
                    " ----------RG----" +
                    "  --OO----------" +
                    "   ------------" +
                    "    ----------")
        assertEquals(state.board, state.board.clone())
    }
    
    @Ignore
    @Test
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
    fun redBeeSourroundedTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "    RQBQ------" +
                "   BB----------" +
                "  --BB----------" +
                " ----------------" +
                "------------------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        run {
            state.currentPlayerColor = PlayerColor.BLUE
            val move = DragMove(CubeCoordinates(-1, 3), CubeCoordinates(0, 3))
            GameRuleLogic.performMove(state, move);
            assertTrue(GameRuleLogic.isBeeBlocked(state.board, PlayerColor.RED))
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
            assertTrue(GameRuleLogic.isBeeBlocked(state.board, PlayerColor.RED))
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
            assertFalse(GameRuleLogic.isBeeBlocked(state.board, PlayerColor.RED))
        }
    }
    
    @Test
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
            val invalid1 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(0, 0))
            assertThrows(InvalidMoveException::class.java) {
                GameRuleLogic.validateMove(state, invalid1)
                val invalid2 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-3, 0))
                assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid2) }
            }
            val valid1 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-4, 4))
            assertTrue(GameRuleLogic.validateMove(state, valid1))
        }
    }
    
    @Test
    fun setMoveNextToOpponentTest() {
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
            val invalid = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-2, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid) }
        }
    }
    
    @Test
    fun setMoveBlockedFieldTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGBGOO----------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val invalid1 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-3, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid1) }
            val invalid2 = SetMove(state.getUndeployedPieces(PlayerColor.RED)[0], CubeCoordinates(-1, 2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid2) }
        }
    }
    
    @Test
    fun setMoveForceBeeTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " RGRGRG----------" +
                    "------------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.turn = 6
            val setAnt = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(-4, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, setAnt) }
            val setBee = SetMove(Piece(PlayerColor.RED, PieceType.BEE), CubeCoordinates(-4, 4))
            assertTrue(GameRuleLogic.validateMove(state, setBee))
        }
    }
    
    @Test
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
    fun dragMoveBeeRequiredTest() {
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
    fun dragMoveBeetleDisconnectTest() {
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
                    " ------BA--------" +
                    "------RARBBA------" +
                    " ------BQRQ------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }
    
    
    @Test
    fun dragMoveBeetleNoJumpTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------RG----" +
                    " --------BG--RB--" +
                    "----------RQBQ----" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(3, -2), CubeCoordinates(3, -1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveBeetleClimbTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "    ----------" +
                "   ------------" +
                "  --------------" +
                " --------RQ------" +
                "--------RB--------" +
                " ----------------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
        assertTrue(GameRuleLogic.validateMove(state, move))
    }
    
    @Test
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
    fun dragMoveAntAroundObstacleTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "    ----------" +
                "   ------------" +
                "  --------------" +
                " --------OO------" +
                "------RQRA--------" +
                " ------OO--------" +
                "  --------------" +
                "   ------------" +
                "    ----------")
        val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(0, 1))
        assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
        val move2 = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
        assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move2) }
        val move3 = DragMove(CubeCoordinates(0, 0), CubeCoordinates(-1, 0))
        assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move3) }
    }
    
    @Test
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
    fun dragMoveAntAroundBorderTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  RARQ----------" +
                    " --BQ------------" +
                    "--OO--------------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(-2, 4), CubeCoordinates(-4, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move) }
            val move2 = DragMove(CubeCoordinates(-2, 4), CubeCoordinates(0, 4))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, move2) }
            val move3 = DragMove(CubeCoordinates(-2, 4), CubeCoordinates(-2, 2))
            assertTrue(GameRuleLogic.validateMove(state, move3))
        }
    }
    
    @Test
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
            val valid1 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, 1))
            assertTrue(GameRuleLogic.validateMove(state, valid1))
            val valid2 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-2, 1))
            assertTrue(GameRuleLogic.validateMove(state, valid2))
            val invalid1 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid1) }
            val invalid2 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid2) }
            val invalid3 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid3) }
            val invalid4 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid4) }
            val invalid5 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(3, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid5) }
            val invalid6 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid6) }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  ----BB--BB----" +
                    " ----RBRS--BS----" +
                    "------RBRQBQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val valid1 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, 1))
            assertTrue(GameRuleLogic.validateMove(state, valid1))
            val valid2 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(3, 0))
            assertTrue(GameRuleLogic.validateMove(state, valid2))
            val valid3 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 2))
            assertTrue(GameRuleLogic.validateMove(state, valid3))
            val valid4 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 3))
            assertTrue(GameRuleLogic.validateMove(state, valid4))
            val invalid1 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 0))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid1) }
            val invalid2 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid2) }
            val invalid3 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 2))
            assertThrows(InvalidMoveException::class.java) { GameRuleLogic.validateMove(state, invalid3) }
        }
    }
    
    @Test
    fun dragMoveEdgeTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --RBRGBGBB----" +
                    " RQBGBSRS--BS----" +
                    "--RS--RBRABQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            val move = DragMove(CubeCoordinates(-3, 4), CubeCoordinates(-2, 4))
            assertTrue(GameRuleLogic.validateMove(state, move))
        }
    }

    @Test
    // ignored because deep equals for gamestate is not implemented completely yet (I think)
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
        TestGameUtil.updateUndeployedPiecesFromBoard(state, true)
        assertEquals(listOf(Piece(PlayerColor.RED, PieceType.ANT)), state.getDeployedPieces(PlayerColor.RED))
        assertEquals(listOf(Piece(PlayerColor.BLUE, PieceType.GRASSHOPPER), Piece(PlayerColor.BLUE, PieceType.BEE)), state.getDeployedPieces(PlayerColor.BLUE))
        assertEquals(PieceType.BEE, state.board.getField(0, 0).pieces.lastElement().type)
        assertEquals(PieceType.BEE, state.board.getField(0, 0).pieces.peek().type)
        val xstream = Configuration.xStream
        val xml = """
            |<state startPlayerColor="RED" currentPlayerColor="RED" turn="0">
            |  <red displayName="" color="RED"/>
            |  <blue displayName="" color="BLUE"/>
            |  <board>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <field x="-4" y="0" z="4" isObstructed="false"/>
            |      <field x="-4" y="1" z="3" isObstructed="false"/>
            |      <field x="-4" y="2" z="2" isObstructed="false"/>
            |      <field x="-4" y="3" z="1" isObstructed="false"/>
            |      <field x="-4" y="4" z="0" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <field x="-3" y="-1" z="4" isObstructed="false"/>
            |      <field x="-3" y="0" z="3" isObstructed="false"/>
            |      <field x="-3" y="1" z="2" isObstructed="false"/>
            |      <field x="-3" y="2" z="1" isObstructed="false"/>
            |      <field x="-3" y="3" z="0" isObstructed="false"/>
            |      <field x="-3" y="4" z="-1" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <null/>
            |      <field x="-2" y="-2" z="4" isObstructed="false"/>
            |      <field x="-2" y="-1" z="3" isObstructed="false"/>
            |      <field x="-2" y="0" z="2" isObstructed="false"/>
            |      <field x="-2" y="1" z="1" isObstructed="false"/>
            |      <field x="-2" y="2" z="0" isObstructed="false"/>
            |      <field x="-2" y="3" z="-1" isObstructed="false">
            |        <piece owner="BLUE" type="GRASSHOPPER"/>
            |      </field>
            |      <field x="-2" y="4" z="-2" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <null/>
            |      <field x="-1" y="-3" z="4" isObstructed="false"/>
            |      <field x="-1" y="-2" z="3" isObstructed="false"/>
            |      <field x="-1" y="-1" z="2" isObstructed="false"/>
            |      <field x="-1" y="0" z="1" isObstructed="false"/>
            |      <field x="-1" y="1" z="0" isObstructed="false"/>
            |      <field x="-1" y="2" z="-1" isObstructed="false"/>
            |      <field x="-1" y="3" z="-2" isObstructed="false"/>
            |      <field x="-1" y="4" z="-3" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <field x="0" y="-4" z="4" isObstructed="false"/>
            |      <field x="0" y="-3" z="3" isObstructed="false"/>
            |      <field x="0" y="-2" z="2" isObstructed="false"/>
            |      <field x="0" y="-1" z="1" isObstructed="false"/>
            |      <field x="0" y="0" z="0" isObstructed="false">
            |        <piece owner="RED" type="ANT"/>
            |        <piece owner="BLUE" type="BEE"/>
            |      </field>
            |      <field x="0" y="1" z="-1" isObstructed="false"/>
            |      <field x="0" y="2" z="-2" isObstructed="false"/>
            |      <field x="0" y="3" z="-3" isObstructed="false"/>
            |      <field x="0" y="4" z="-4" isObstructed="false"/>
            |    </fields>
            |    <fields>
            |      <field x="1" y="-4" z="3" isObstructed="false"/>
            |      <field x="1" y="-3" z="2" isObstructed="false"/>
            |      <field x="1" y="-2" z="1" isObstructed="false"/>
            |      <field x="1" y="-1" z="0" isObstructed="false"/>
            |      <field x="1" y="0" z="-1" isObstructed="false"/>
            |      <field x="1" y="1" z="-2" isObstructed="false"/>
            |      <field x="1" y="2" z="-3" isObstructed="false"/>
            |      <field x="1" y="3" z="-4" isObstructed="false"/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="2" y="-4" z="2" isObstructed="false"/>
            |      <field x="2" y="-3" z="1" isObstructed="false"/>
            |      <field x="2" y="-2" z="0" isObstructed="false"/>
            |      <field x="2" y="-1" z="-1" isObstructed="false"/>
            |      <field x="2" y="0" z="-2" isObstructed="false"/>
            |      <field x="2" y="1" z="-3" isObstructed="false"/>
            |      <field x="2" y="2" z="-4" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="3" y="-4" z="1" isObstructed="false"/>
            |      <field x="3" y="-3" z="0" isObstructed="false"/>
            |      <field x="3" y="-2" z="-1" isObstructed="false"/>
            |      <field x="3" y="-1" z="-2" isObstructed="false"/>
            |      <field x="3" y="0" z="-3" isObstructed="false"/>
            |      <field x="3" y="1" z="-4" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |      <null/>
            |    </fields>
            |    <fields>
            |      <field x="4" y="-4" z="0" isObstructed="false"/>
            |      <field x="4" y="-3" z="-1" isObstructed="false"/>
            |      <field x="4" y="-2" z="-2" isObstructed="false"/>
            |      <field x="4" y="-1" z="-3" isObstructed="false"/>
            |      <field x="4" y="0" z="-4" isObstructed="false"/>
            |      <null/>
            |      <null/>
            |      <null/>
            |      <null/>
            |    </fields>
            |  </board>
            |  <undeployedRedPieces>
            |    <piece owner="RED" type="BEE"/>
            |    <piece owner="RED" type="SPIDER"/>
            |    <piece owner="RED" type="SPIDER"/>
            |    <piece owner="RED" type="SPIDER"/>
            |    <piece owner="RED" type="GRASSHOPPER"/>
            |    <piece owner="RED" type="GRASSHOPPER"/>
            |    <piece owner="RED" type="BEETLE"/>
            |    <piece owner="RED" type="BEETLE"/>
            |    <piece owner="RED" type="ANT"/>
            |    <piece owner="RED" type="ANT"/>
            |  </undeployedRedPieces>
            |  <undeployedBluePieces>
            |    <piece owner="BLUE" type="SPIDER"/>
            |    <piece owner="BLUE" type="SPIDER"/>
            |    <piece owner="BLUE" type="SPIDER"/>
            |    <piece owner="BLUE" type="GRASSHOPPER"/>
            |    <piece owner="BLUE" type="BEETLE"/>
            |    <piece owner="BLUE" type="BEETLE"/>
            |    <piece owner="BLUE" type="ANT"/>
            |    <piece owner="BLUE" type="ANT"/>
            |    <piece owner="BLUE" type="ANT"/>
            |  </undeployedBluePieces>
            |</state>""".trimMargin()
        assertEquals(xml, xstream.toXML(state))
        val fromXml = xstream.fromXML(xml) as GameState
        assertEquals(state.board, fromXml.board)
        assertEquals(state.getUndeployedPieces(PlayerColor.RED), fromXml.getUndeployedPieces(PlayerColor.RED))
        assertEquals(state.getUndeployedPieces(PlayerColor.BLUE), fromXml.getUndeployedPieces(PlayerColor.BLUE))
    }
    
    @Test
    fun moveToXmlTest() {
        val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        val roomId = "42"
        val xstream = Configuration.xStream
        val xml = xstream.toXML(RoomPacket(roomId, move))
        val expect = """
            |<room roomId="$roomId">
            |  <data class="setmove">
            |    <piece owner="RED" type="ANT"/>
            |    <destination x="1" y="2" z="-3"/>
            |  </data>
            |</room>""".trimMargin()
        assertEquals(expect, xml)
    }
    
    @Test
    fun xmlToDragMoveTest() {
        val xstream = Configuration.xStream
        val xml = """
            <room roomId="42">
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
        val xml = """
            <room roomId="64a0482c-f368-4e33-9684-d5106228bb75">
              <data class="setmove">
                <piece owner="RED" type="BEETLE" />
                <destination x="-2" y="0" z="2"/>
              </data>
            </room>"""
        val packet = xstream.fromXML(xml) as RoomPacket
        val expect = SetMove(Piece(PlayerColor.RED, PieceType.BEETLE), CubeCoordinates(-2, 0, 2))
        assertEquals(expect, packet.data)
    }
    
    @Test
    fun performMoveTest() {
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
        val move = SetMove(Piece(PlayerColor.RED, PieceType.ANT), CubeCoordinates(1, 2, -3))
        GameRuleLogic.performMove(state, move)
        assertEquals(PieceType.ANT, state.board.getField(1, 2, -3).pieces.lastElement().type)
    }
    
    @Test
    fun possibleMoveTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --RBRGBGBB----" +
                    " RQBGBSRS--BS----" +
                    "--RS--RBRABQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertFalse(GameRuleLogic.getPossibleMoves(state).isEmpty())
        }
    }
    
    @Test
    fun possibleSetMoveDestinationsTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --RBRGBGBB----" +
                    " RQBGBSRS--BS----" +
                    "--RS--RBRABQ------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertEquals(7, GameRuleLogic.getPossibleSetMoveDestinations(state.board, state.currentPlayerColor).size)
        }
    }
    
    @Test
    fun possibleSetMoveDestinationsSecondTurnTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "--------RBOO------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            state.currentPlayerColor = PlayerColor.BLUE
            state.turn = 1
            assertEquals(PieceType.values().size * 5, GameRuleLogic.getPossibleSetMoves(state).size)
        }
    }
    
    @Test
    fun possibleSetMoveDestinationsFirstTurnTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ----------------" +
                    "----------OO------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertEquals(PieceType.values().size * (Constants.FIELD_AMOUNT - 1), GameRuleLogic.getPossibleSetMoves(state).size)
        }
    }
    
    @Test
    fun possibleDragMovesTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "    ----------" +
                    "   ------------" +
                    "  --------------" +
                    " ------BB--------" +
                    "--------RQOO------" +
                    " ----------------" +
                    "  --------------" +
                    "   ------------" +
                    "    ----------")
            assertEquals(1, GameRuleLogic.getPossibleDragMoves(state).size)
        }
    }
}
