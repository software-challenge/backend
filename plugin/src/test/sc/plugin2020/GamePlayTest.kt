package sc.plugin2020

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldNotContain
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AnnotationSpec
import org.junit.Assert.*
import sc.plugin2020.util.Constants
import sc.plugin2020.util.CubeCoordinates
import sc.plugin2020.util.GameRuleLogic
import sc.plugin2020.util.TestGameUtil
import sc.shared.InvalidMoveException
import sc.shared.Team
import java.security.InvalidParameterException

class GamePlayTest: AnnotationSpec() {
    
    private lateinit var state: GameState
    
    @BeforeEach
    fun beforeEveryTest() {
        state = GameState()
    }
    
    @Test
    fun invalidBoardStringTest() {
        shouldThrow<InvalidParameterException> {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     XB----------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
        }
        shouldThrow<InvalidParameterException> {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     BY----------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
        }
    }
    
    @Test
    fun boardCloneTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----BBOOBB------" +
                "  ----RBRS--BS------" +
                " ------RBRQBQ--------" +
                "------------RG--------" +
                " ----OO--------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        assertEquals(state.board, state.board.clone())
    }
    
    @Test
    fun redBeeBlockedTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     RQBQ--------" +
                    "    BB------------" +
                    "   --BB------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            state.turn = 1
            val move = DragMove(CubeCoordinates(-1, 4), CubeCoordinates(0, 4))
            GameRuleLogic.performMove(state, move)
            assertTrue(GameRuleLogic.isBeeBlocked(state.board, Team.ONE))
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ----BBBB----------" +
                    " ----BBRQBB----------" +
                    "------BBBB------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            assertTrue(GameRuleLogic.isBeeBlocked(state.board, Team.ONE))
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ----BBBB----------" +
                    " ------RQBB----------" +
                    "------BBBB------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            assertFalse(GameRuleLogic.isBeeBlocked(state.board, Team.ONE))
        }
    }
    
    @Test
    fun setMoveOnEmptyBoardTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(0, 0))
            GameRuleLogic.validateMove(state, move)
        }
    }
    
    @Test
    fun setMoveOutsideOfBoard() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(8, 0))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun setMoveOfUnavailablePieceTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  RGBG--------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            state.getUndeployedPieces(Team.ONE).clear()
            val move = SetMove(Piece(Team.ONE, PieceType.ANT), CubeCoordinates(-4, 4))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun setMoveConnectionToSwarmTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  RGBG--------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val invalid1 = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(0, 0))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, invalid1) }
            val invalid2 = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(-3, 4))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, invalid2) }
            val valid1 = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(-4, 5))
            GameRuleLogic.validateMove(state, valid1)
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  --BG--------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(0, 0))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun setMoveNextToOpponentTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  RGBG--------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val invalid = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(-2, 4))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, invalid) }
        }
    }
    
    @Test
    fun setMoveBlockedFieldTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  RGBGOO------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val invalid1 = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(-3, 4))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, invalid1) }
            val invalid2 = SetMove(state.getUndeployedPieces(Team.ONE)[0], CubeCoordinates(-1, 2))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, invalid2) }
        }
    }
    
    @Test
    fun setMoveForceBeeTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  RGRGRG------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            state.turn = 6
            val setAnt = SetMove(Piece(Team.ONE, PieceType.ANT), CubeCoordinates(-4, 5))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, setAnt) }
            val skip = SkipMove
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, skip) }
            val setBee = SetMove(Piece(Team.ONE, PieceType.BEE), CubeCoordinates(-4, 5))
            GameRuleLogic.validateMove(state, setBee)
        }
    }
    
    @Test
    fun dragMoveNonexistentPieceTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(0, 0))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveOntoOtherPieceTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------RQBQ--------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, -1))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveBeeRequiredTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------RBBG--------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 0))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  --------BA--------" +
                    " --------RB----------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(1, 0))
            GameRuleLogic.validateMove(state, move)
        }
    }
    
    @Test
    fun dragMoveBeeValidTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------RBBG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 1))
            GameRuleLogic.validateMove(state, move)
        }
    }
    
    @Test
    fun dragMoveBeeTooFarTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------RBBG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, -1), CubeCoordinates(2, -2))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveBeetleTooFarTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------RBBG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 0), CubeCoordinates(2, -1))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveBeetleJumpTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------BA----------" +
                    " ------RARBBA--------" +
                    "--------BQRQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
            GameRuleLogic.validateMove(state, move)
        }
    }
    
    @Test
    fun dragMoveBeetleDisconnectTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------BA----------" +
                    " --------RBBG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   --------RG------" +
                    "  --------BG--RB----" +
                    " ----------RQBQ------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val possibleMoves = GameRuleLogic.getPossibleDragMoves(state)
            arrayOf(
                    DragMove(CubeCoordinates(3, -1), CubeCoordinates(3, 0)),
                    DragMove(CubeCoordinates(3, -1), CubeCoordinates(4, 0))
            ).forEach { move ->
                shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
                possibleMoves shouldNotContain move
            }
        }
    }
    
    @Test
    fun dragMoveBeetleClimbTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  --------RQ--------" +
                " --------RB----------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
        GameRuleLogic.validateMove(state, move)
    }
    
    @Test
    fun dragMoveBeetleObstructedTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     RBOO--------" +
                "    --BGRQ--------" +
                "   ----------------" +
                "  ------------------" +
                " --------------------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        val moveBeetle = DragMove(CubeCoordinates(0, 5), CubeCoordinates(1, 4))
        shouldThrow<InvalidMoveException> { GameRuleLogic.performMove(state, moveBeetle) }
    }
    
    @Test
    fun dragMoveGrasshopperValidTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------BABB--------" +
                    " ------BQRBRG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(1, 0), CubeCoordinates(-2, 3))
            GameRuleLogic.validateMove(state, move)
            val move2 = DragMove(CubeCoordinates(1, 0), CubeCoordinates(1, 2))
            GameRuleLogic.validateMove(state, move2)
        }
    }
    
    @Test
    fun dragMoveGrasshopperOverBlockedFieldTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------RQBB--------" +
                    " --------OORG--------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(1, 0), CubeCoordinates(-1, 2))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  --RBRQBABB--------" +
                    " ----OOBQRBRG--------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(1, 0), CubeCoordinates(-3, 4))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveGrasshopperOverEmptyFieldTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------BABB--------" +
                    " ------BQRBRG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(1, 0), CubeCoordinates(-3, 4))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveGrasshopperToNeighbourTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------BABB--------" +
                    " ------BQRBRG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(1, 0), CubeCoordinates(1, -1))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveAntValidTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------RABB--------" +
                    " ------BQRBRG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 2), CubeCoordinates(1, 2))
            GameRuleLogic.validateMove(state, move)
            val move2 = DragMove(CubeCoordinates(0, 2), CubeCoordinates(0, -1))
            GameRuleLogic.validateMove(state, move2)
        }
    }
    
    @Test
    fun dragMoveAntAroundObstacleTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  --------OO--------" +
                " ------RQRA----------" +
                "--------OO------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 2))
        shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        val move2 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1))
        shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move2) }
        val move3 = DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 0))
        shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move3) }
    }
    
    @Test
    fun dragMoveAntIntoBlockedTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   --------RB------" +
                    "  ------RABB--BB----" +
                    " ------BQRBRGBB------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, -1))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveAntAroundBorderTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   RARQ------------" +
                    "  --BQ--------------" +
                    " --OO----------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(-2, 5), CubeCoordinates(-4, 5))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
            val move2 = DragMove(CubeCoordinates(-2, 5), CubeCoordinates(0, 5))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move2) }
            val move3 = DragMove(CubeCoordinates(-2, 5), CubeCoordinates(-2, 3))
            GameRuleLogic.validateMove(state, move3)
        }
    }
    
    @Test
    fun dragMoveAntDisconnectFromSwarmTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------RABB--------" +
                    " ------BQ--RG--------" +
                    "----------RQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(0, 2), CubeCoordinates(-1, 1))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
        }
    }
    
    @Test
    fun dragMoveSpiderTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ----------BB------" +
                    " --------RS--BS------" +
                    "--------RBRQBQ--------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val possibleMoves = GameRuleLogic.getPossibleDragMoves(state)
            arrayOf(
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, 1)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(-2, 1))
            ).forEach {
                GameRuleLogic.validateMove(state, it)
                possibleMoves shouldContain it
            }
            arrayOf(
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 0)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 2)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 2)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(3, 0)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 0))
            ).forEach {
                shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, it) }
                possibleMoves shouldNotContain it
            }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ------BB--BB------" +
                    " ------RBRS--BS------" +
                    "--------RBRQBQ--------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val possibleMoves = GameRuleLogic.getPossibleDragMoves(state)
            arrayOf(
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(2, 1)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(3, 0)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 2)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(0, 3))
            ).forEach {
                GameRuleLogic.validateMove(state, it)
                possibleMoves shouldContain it
            }
            arrayOf(
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 0)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(1, 1)),
                    DragMove(CubeCoordinates(0, 1), CubeCoordinates(-1, 2))
            ).forEach {
                shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, it) }
                possibleMoves shouldNotContain it
            }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    ----------RQRS" +
                    "   ------------RB--" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(5, -1, -4), CubeCoordinates(5, -2, -3))
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, move) }
            GameRuleLogic.getPossibleDragMoves(state) shouldNotContain move
        }
    }
    
    @Test
    fun dragMoveEdgeTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  --RBRGBGBB--------" +
                    " RQBGBSRS--BS--------" +
                    "--RS--RBRABQ----------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val move = DragMove(CubeCoordinates(-4, 5), CubeCoordinates(-3, 5))
            GameRuleLogic.validateMove(state, move)
        }
    }
    
    @Test
    fun performMoveTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  ------------------" +
                " --------------------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        val move = SetMove(Piece(Team.ONE, PieceType.ANT), CubeCoordinates(1, 2, -3))
        GameRuleLogic.performMove(state, move)
        assertEquals(PieceType.ANT, state.board.getField(1, 2, -3).topPiece?.type)
    }
    
    @Test
    fun possibleMoveTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   --RBRGBGBB------" +
                "  RQBGBSRS--BS------" +
                " --RS--RBRABQ--------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        assertFalse(GameRuleLogic.getPossibleMoves(state).isEmpty())
    }
    
    @Test
    fun possibleSetMoveDestinationsTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   --RBRGBGBB------" +
                "  RQBGBSRS--BS------" +
                " --RS--RBRABQ--------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        assertEquals(7, GameRuleLogic.getPossibleSetMoveDestinations(state.board, state.currentPlayerColor).size)
    }
    
    @Test
    fun possibleSetMoveDestinationsSecondTurnTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  ------------------" +
                " --------RBOO--------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        state.turn = 1
        assertEquals(PieceType.values().size * 5, GameRuleLogic.getPossibleSetMoves(state).size)
    }
    
    @Test
    fun possibleSetMoveDestinationsFirstTurnTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  ------------------" +
                " ----------OO--------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        assertEquals(PieceType.values().size * (Constants.FIELD_AMOUNT - 1), GameRuleLogic.getPossibleSetMoves(state).size)
    }
    
    @Test
    fun possibleDragMoveObstructedTest() {
        TestGameUtil.updateGamestateWithBoard(state, "" +
                "     ------------" +
                "    --------------" +
                "   ----------------" +
                "  ------BB----------" +
                " --------RQOO--------" +
                "----------------------" +
                " --------------------" +
                "  ------------------" +
                "   ----------------" +
                "    --------------" +
                "     ------------")
        assertEquals(1, GameRuleLogic.getPossibleDragMoves(state).size)
    }
    
    @Test
    fun possibleMoveSeparateTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  ----------------RQ" +
                    " ------------------BQ" +
                    "--------------------RA" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            state.turn = 3
            GameRuleLogic.getPossibleMoves(state).forEach {
                GameRuleLogic.validateMove(state, it)
            }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    ------------BA" +
                    "   --------------BB" +
                    "  ----------RGRQBSBB" +
                    " ------------RB----BQ" +
                    "--------------RS----BA" +
                    " ------------RG------" +
                    "  ------------RA----" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            state.turn = 9
            GameRuleLogic.getPossibleMoves(state).forEach {
                GameRuleLogic.validateMove(state, it)
            }
        }
    }
    
    @Test
    fun skipMoveTest() {
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     ------------" +
                    "    --------------" +
                    "   ----------------" +
                    "  RGBG--------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val invalid = SkipMove
            shouldThrow<InvalidMoveException> { GameRuleLogic.validateMove(state, invalid) }
        }
        run {
            TestGameUtil.updateGamestateWithBoard(state, "" +
                    "     RA----------" +
                    "    --BG----------" +
                    "   ----------------" +
                    "  ------------------" +
                    " --------------------" +
                    "----------------------" +
                    " --------------------" +
                    "  ------------------" +
                    "   ----------------" +
                    "    --------------" +
                    "     ------------")
            val valid = SkipMove
            GameRuleLogic.validateMove(state, valid)
            assertEquals(GameRuleLogic.getPossibleMoves(state), listOf(SkipMove))
        }
    }
    
}
