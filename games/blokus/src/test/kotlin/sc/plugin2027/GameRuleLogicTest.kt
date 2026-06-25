package sc.plugin2027

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import sc.api.plugins.Coordinates
import sc.plugin2027.util.Constants
import sc.plugin2027.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameRuleLogicTest: WordSpec({
    "Moves" When {
        val gameState = GameState(startPiece = PieceShape.PENTO_U)
        "the color is compared" should {
            "fail if this color isn't active yet" {
                shouldThrow<InvalidMoveException> {
                    val invalidMove = SetMove(Piece(Color.RED))
                    GameRuleLogic.performMove(gameState, invalidMove)
                }
            }
            "succeed if the color is currently active" {
                shouldNotThrow<InvalidMoveException> {
                    val validMove = SetMove(Piece(Color.BLUE, PieceShape.PENTO_U))
                    GameRuleLogic.performMove(gameState, validMove)
                }
            }
        }
        "they are placed out of bounds" should {
            val invalidMove = SetMove(Piece(Color.BLUE, PieceShape.MONO, Rotation.NONE, false, Coordinates(-1, 2)))
            "throw a InvalidMoveException" {
                shouldThrow<InvalidMoveException> {
                    GameRuleLogic.validateSetMove(gameState, invalidMove)
                }
            }
        }
        "it's the first turn" should {
            "throw if they skip" {
                shouldThrow<InvalidMoveException> {
                    GameRuleLogic.performMove(gameState, SkipMove(Color.BLUE))
                }
            }
            "throw if the shape is invalid" {
                shouldThrow<InvalidMoveException> {
                    GameRuleLogic.performMove(gameState, SetMove(
                            Piece(Color.BLUE, PieceShape.PENTO_S)
                    ))
                }
            }
            "throw if the position is invalid" {
                for (piece in listOf(
                        Piece(Color.BLUE, PieceShape.PENTO_U, position = Coordinates(-1, -1)),
                        Piece(Color.BLUE, PieceShape.PENTO_U, position = Coordinates(10, 10))
                )) {
                    shouldThrow<InvalidMoveException> {
                        GameRuleLogic.performMove(gameState, SetMove(piece))
                    }
                }
            }
            "succeed otherwise" {
                for (piece in listOf(
                        Piece(Color.BLUE, PieceShape.PENTO_U, position = Coordinates(Constants.BOARD_LENGTH- 3, 0)),
                        Piece(Color.YELLOW, PieceShape.PENTO_U, Rotation.RIGHT, position = Coordinates(Constants.BOARD_LENGTH - 2, Constants.BOARD_LENGTH - 3)),
                        Piece(Color.RED, PieceShape.PENTO_U, position = Coordinates(0, Constants.BOARD_LENGTH - 2)),
                        Piece(Color.GREEN, PieceShape.PENTO_U, isFlipped = true)
                )) {
                    shouldNotThrow<InvalidMoveException> {
                        GameRuleLogic.performMove(gameState, SetMove(piece))
                    }
                }
            }
        }
    }
    "Score calculation" When {
        "the player placed all pieces" should {
            "return the max score (109) if MONO was last" {
                GameRuleLogic.getPointsFromUndeployed(emptySet(), true) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES + 15 + 5
            }
            "return 104 if the MONO was not last" {
                GameRuleLogic.getPointsFromUndeployed(emptySet(), false) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES + 15
            }
        }
        "no piece was placed" should {
            "return 0 points" {
                GameRuleLogic.getPointsFromUndeployed(PieceShape.entries.toSet()) shouldBe 0
                GameRuleLogic.getPointsFromUndeployed(PieceShape.entries.toSet(), true) shouldBe 0
            }
        }
        "a few pieces were placed" should {
            val fewPieces = setOf(PieceShape.MONO, PieceShape.PENTO_W, PieceShape.TETRO_I)
            "return the sum of all placed pieces" {
                GameRuleLogic.getPointsFromUndeployed(fewPieces) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES - fewPieces.sumOf { it.coordinates.size }
                GameRuleLogic.getPointsFromUndeployed(fewPieces, true) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES - fewPieces.sumOf { it.coordinates.size }
            }
        }
    }
    "Possible start move calculation" should {
        val piece = PieceShape.PENTO_W
        var state = GameState(startPiece = piece)
        // Count the number of possible moves.
        // Note, that each variant piece has its origin in the top-left corner.
        val expected = (4 // All real variants of the piece
                    * 4 // 4 sides to consider
                    * 17 // 18 places on a side - 1 to remove duplicates for other side
        )
        val actualMoves = state.getSensibleMoves()
        "has no duplicates" {
            actualMoves.size shouldBe actualMoves.toSet().size
        }
        // I suspect that the problem is how a piece is moved around its origin
        val actual = actualMoves.size
        "return the correct number of moves for the starting piece" {
            actual shouldBe expected
        }
        "can execute every start move calculated" {
        
        }
    }
    "Can send SkipMove" should {
        // Make sure it is not a start turn (there you cannot skip?)
        val state = GameState(turn = 20)
        // Remove all pieces for one color
        state.undeployedPieceShapes(state.currentColor).map { Piece(state.currentColor, it) }.forEach { state.removeUndeployedPiece(it) }
        val moves = state.getSensibleMoves()
        // If no pieces are left, the only move should be to skip
        moves shouldBe listOf(SkipMove(state.currentColor))
    }
})
