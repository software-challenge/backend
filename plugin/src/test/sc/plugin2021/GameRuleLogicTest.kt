package sc.plugin2021

import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.plugin2021.util.filterValidMoves
import sc.shared.InvalidMoveException

class GameRuleLogicTest: StringSpec({
    "Color validation works correctly" {
        val gameState = GameState(startPiece = PieceShape.PENTO_U)

        assertThrows<InvalidMoveException> {
            val invalidMove = SetMove(Piece(Color.RED))
            GameRuleLogic.performMove(gameState, invalidMove)
        }
        assertDoesNotThrow {
            val validMove = SetMove(
                    Piece(Color.BLUE, PieceShape.PENTO_U))
            GameRuleLogic.performMove(gameState, validMove)
        }
    }
    "Position validation works" {
        val gameState: GameState = GameState()
        gameState.board[Coordinates(1, 1)] = FieldContent.BLUE

        assertThrows<InvalidMoveException> {
            val invalidMove = SetMove(
                    Piece(Color.BLUE, PieceShape.MONO, Rotation.NONE, false, Coordinates(-1, 2)))
            GameRuleLogic.validateSetMove(gameState, invalidMove)
        }
        GameRuleLogic.isObstructed(gameState.board, Coordinates(1, 1)) shouldBe true
        GameRuleLogic.isObstructed(gameState.board, Coordinates(0, 0)) shouldNotBe true

        GameRuleLogic.bordersOnColor(gameState.board, Coordinates(1, 0), Color.BLUE) shouldBe true
        GameRuleLogic.bordersOnColor(gameState.board, Coordinates(0, 0), Color.BLUE) shouldNotBe true

        GameRuleLogic.cornersOnColor(gameState.board, Coordinates(0, 0), Color.BLUE) shouldBe true
        GameRuleLogic.cornersOnColor(gameState.board, Coordinates(0, 0), Color.GREEN) shouldNotBe true
        GameRuleLogic.cornersOnColor(gameState.board, Coordinates(1, 0), Color.BLUE) shouldNotBe true
    }
    "The first piece's special rules work" {
        // PENTO_S is:   # # #
        //           : # #
        val invalidPieces = listOf(
                Piece(Color.GREEN,  PieceShape.TETRO_O),
                Piece(Color.BLUE,   PieceShape.PENTO_S),
                Piece(Color.YELLOW, PieceShape.PENTO_S, Rotation.RIGHT, position = Coordinates(Constants.BOARD_SIZE - 4, 0)),
                Piece(Color.RED,    PieceShape.PENTO_S, position = Coordinates(13, 5)),
                Piece(Color.GREEN,  PieceShape.PENTO_S, Rotation.LEFT, position = Coordinates(Constants.BOARD_SIZE - 2, 0)),
                Piece(Color.BLUE,   PieceShape.PENTO_S, position = Coordinates(Constants.BOARD_SIZE - 4, 0)) // valid but will be obstructed
        )
        val validPieces = listOf(
                Piece(Color.BLUE,   PieceShape.PENTO_S, position = Coordinates(Constants.BOARD_SIZE - 4, 0)),
                Piece(Color.YELLOW, PieceShape.PENTO_S, Rotation.RIGHT, position = Coordinates(Constants.BOARD_SIZE - 2, Constants.BOARD_SIZE - 4)),
                Piece(Color.RED,    PieceShape.PENTO_S, position = Coordinates(0, Constants.BOARD_SIZE - 2)),
                Piece(Color.GREEN,  PieceShape.PENTO_S, isFlipped = true)
        )

        assertDoesNotThrow {
            val gameState = GameState(startPiece = PieceShape.PENTO_S)
            GameRuleLogic.performMove(gameState, SetMove(validPieces.first()))
            assertThrows<InvalidMoveException> {
                invalidPieces.forEach {
                    GameRuleLogic.performMove(gameState, SetMove(it))
                }
            }
        }
        assertDoesNotThrow {
            val gameState = GameState(startPiece = PieceShape.PENTO_S)
            validPieces.forEach {
                GameRuleLogic.performMove(gameState, SetMove(it))
            }
        }
    }
    "Point score calculation works" {
        GameRuleLogic.getPointsFromDeployedPieces(emptyList()) shouldBe GameRuleLogic.SMALLEST_SCORE_POSSIBLE

        val fewPieces = listOf(
                Piece(Color.BLUE, PieceShape.TRIO_I,  Rotation.NONE, true),
                Piece(Color.BLUE, PieceShape.TETRO_O, Rotation.NONE, true),
                Piece(Color.BLUE, PieceShape.PENTO_P, Rotation.NONE, true),
                Piece(Color.BLUE, PieceShape.MONO,    Rotation.NONE, true)
        )
        GameRuleLogic.getPointsFromDeployedPieces(fewPieces) shouldBe -76

        val allPieces = PieceShape.shapes.map{
            Piece(Color.BLUE, it.key, Rotation.NONE, false)
        }.toList()
        GameRuleLogic.getPointsFromDeployedPieces(allPieces) shouldBe 15

        val perfectPieces = allPieces.reversed()
        GameRuleLogic.getPointsFromDeployedPieces(perfectPieces) shouldBe 20
    }
    "PassMoves let you pass out" {
        val gameState = GameState()
        assertThrows<InvalidMoveException> {
            GameRuleLogic.performMove(gameState, PassMove(Color.RED))
        }
        assertDoesNotThrow {
            GameRuleLogic.performMove(gameState, PassMove(Color.BLUE))
        }
        gameState.orderedColors.size shouldBe 3
    }
    "All possible start moves get calculated" {
        val piece = PieceShape.PENTO_W
        var state = GameState(startPiece = piece)
        var SHOULD = setOf(
                Piece(Color.BLUE, piece, Rotation.NONE, false, Coordinates(0, 0)),
                Piece(Color.BLUE, piece, Rotation.MIRROR, false, Coordinates(0, 0)),
                Piece(Color.BLUE, piece, Rotation.RIGHT, false, Coordinates(17, 0)),
                Piece(Color.BLUE, piece, Rotation.LEFT, false, Coordinates(17, 0)),
                Piece(Color.BLUE, piece, Rotation.NONE, false, Coordinates(17, 17)),
                Piece(Color.BLUE, piece, Rotation.MIRROR, false, Coordinates(17, 17)),
                Piece(Color.BLUE, piece, Rotation.RIGHT, false, Coordinates(0, 17)),
                Piece(Color.BLUE, piece, Rotation.LEFT, false, Coordinates(0, 17))
        ).map { SetMove(it) }.toSet()
        var IS = GameRuleLogic.getPossibleMoves(state)
        
        IS shouldContainExactlyInAnyOrder SHOULD
        GameRuleLogic.performMove(state, SHOULD.first())
    
        SHOULD = setOf(
                Piece(Color.YELLOW, piece, Rotation.RIGHT, false, Coordinates(17, 0)),
                Piece(Color.YELLOW, piece, Rotation.LEFT, false, Coordinates(17, 0)),
                Piece(Color.YELLOW, piece, Rotation.NONE, false, Coordinates(17, 17)),
                Piece(Color.YELLOW, piece, Rotation.MIRROR, false, Coordinates(17, 17)),
                Piece(Color.YELLOW, piece, Rotation.RIGHT, false, Coordinates(0, 17)),
                Piece(Color.YELLOW, piece, Rotation.LEFT, false, Coordinates(0, 17))
        ).map { SetMove(it) }.toSet()
        IS = GameRuleLogic.getPossibleMoves(state)
    
        IS shouldContainExactlyInAnyOrder SHOULD
        GameRuleLogic.performMove(state, SHOULD.first())
        
        SHOULD = setOf(
                Piece(Color.RED, piece, Rotation.NONE, false, Coordinates(17, 17)),
                Piece(Color.RED, piece, Rotation.MIRROR, false, Coordinates(17, 17)),
                Piece(Color.RED, piece, Rotation.RIGHT, false, Coordinates(0, 17)),
                Piece(Color.RED, piece, Rotation.LEFT, false, Coordinates(0, 17))
        ).map { SetMove(it) }.toSet()
        IS = GameRuleLogic.getPossibleMoves(state)
        
        IS shouldContainExactlyInAnyOrder SHOULD
        GameRuleLogic.performMove(state, SHOULD.first())
        
        SHOULD = setOf(
                Piece(Color.GREEN, piece, Rotation.RIGHT, false, Coordinates(0, 17)),
                Piece(Color.GREEN, piece, Rotation.LEFT, false, Coordinates(0, 17))
        ).map { SetMove(it) }.toSet()
        IS = GameRuleLogic.getPossibleMoves(state)
        
        IS shouldContainExactlyInAnyOrder SHOULD
    
        state = GameState()
        GameRuleLogic.getPossibleMoves(state) shouldContainExactlyInAnyOrder
                GameRuleLogic.getAllMoves(state).filterValidMoves(state)
    
        GameRuleLogic.getPossibleMoves(state) shouldContainExactlyInAnyOrder
                GameRuleLogic.getPossibleMoves(state).filterValidMoves(state)
    }
    "All possible moves get calculated" {
        // TODO: set up a mid-game state so that the list of possible moves is non-trivial
    }
})