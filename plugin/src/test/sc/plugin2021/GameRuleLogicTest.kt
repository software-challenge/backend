package sc.plugin2021

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.plugin2021.util.filterValidMoves
import sc.shared.InvalidMoveException

class GameRuleLogicTest: WordSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    "Moves" When {
        val gameState = GameState(startPiece = PieceShape.PENTO_U)
        "the color is compared" Should {
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
        "they are placed out of bounds" Should {
            val invalidMove = SetMove(Piece(Color.BLUE, PieceShape.MONO, Rotation.NONE, false, Coordinates(-1, 2)))
            "throw a InvalidMoveException" {
                shouldThrow<InvalidMoveException> {
                    GameRuleLogic.validateSetMove(gameState, invalidMove, true)
                }
            }
        }
        "it's the first turn" Should {
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
                        Piece(Color.BLUE, PieceShape.PENTO_U, position = Coordinates(Constants.BOARD_SIZE - 3, 0)),
                        Piece(Color.YELLOW, PieceShape.PENTO_U, Rotation.RIGHT, position = Coordinates(Constants.BOARD_SIZE - 2, Constants.BOARD_SIZE - 3)),
                        Piece(Color.RED, PieceShape.PENTO_U, position = Coordinates(0, Constants.BOARD_SIZE - 2)),
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
        "the player placed all pieces" Should {
            "return the max score (109) if MONO was last" {
                GameRuleLogic.getPointsFromUndeployed(emptySet(), true) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES + 15 + 5
            }
            "return 104 if the MONO was not last" {
                GameRuleLogic.getPointsFromUndeployed(emptySet(), false) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES + 15
            }
        }
        "no piece was placed" Should {
            "return 0 points" {
                GameRuleLogic.getPointsFromUndeployed(PieceShape.values().toSet()) shouldBe 0
                GameRuleLogic.getPointsFromUndeployed(PieceShape.values().toSet(), true) shouldBe 0
            }
        }
        "a few pieces were placed" Should {
            val fewPieces = setOf(PieceShape.MONO, PieceShape.PENTO_W, PieceShape.TETRO_I)
            "return the sum of all placed pieces" {
                GameRuleLogic.getPointsFromUndeployed(fewPieces) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES - fewPieces.map{it.coordinates.size}.sum()
                GameRuleLogic.getPointsFromUndeployed(fewPieces, true) shouldBe
                        GameRuleLogic.SUM_MAX_SQUARES - fewPieces.map{it.coordinates.size}.sum()
            }
        }
    }
    "Possible start move calculation" should {
        val piece = PieceShape.PENTO_W
        var state = GameState(startPiece = piece)
        "return all possible moves that can be placed in a free corner" {
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
                    GameRuleLogic.getPossibleMoves(state).filterValidMoves(state)
        }
    }
})