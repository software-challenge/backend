package sc.plugin2021

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.helpers.testXStream
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameStateTest: WordSpec({
    "GameStates" When {
        val state = GameState(startPiece = PieceShape.PENTO_I)
        "constructed" should {
            "have an empty board" {
                state.board shouldBe Board()
            }
            "have each PieceShape available for each color" {
                Color.values().forEach { color ->
                    state.undeployedPieceShapes(color) shouldBe PieceShape.values().toSet()
                }
            }
            "start with no points for either player" {
                state.getPointsForPlayer(Team.ONE) shouldBe 0
                state.getPointsForPlayer(Team.TWO) shouldBe 0
            }
        }
        "asked for the current color" should {
            "return the correct color" {
                state.orderedColors.size shouldBe Constants.COLORS
                for (color in Color.values()) {
                    state.currentColor shouldBe color
                    state.turn++
                }

                state.currentColor shouldBe Color.BLUE
                state.turn++
                state.currentColor shouldBe Color.YELLOW
                state.turn += 2
                state.currentColor shouldBe Color.GREEN
            }
        }
        "a piece is placed a second time" should {
            val move = SetMove(Piece(Color.BLUE, PieceShape.PENTO_I, Rotation.RIGHT, true))
            state.undeployedPieceShapes(Color.BLUE).size shouldBe 21
            shouldNotThrow<InvalidMoveException> {
                GameRuleLogic.performMove(state, move)
            }
            state.turn += 4
            state.undeployedPieceShapes(Color.BLUE).size shouldBe 20
            "throw an InvalidMoveException" {
                shouldThrow<InvalidMoveException> {
                    GameRuleLogic.performMove(state, move)
                }
            }
            state.undeployedPieceShapes(Color.BLUE).size shouldBe 20
        }
        "serialised and deserialised" should {
            val xStream = testXStream
            val transformed = xStream.fromXML(xStream.toXML(GameState(startPiece = state.startPiece))) as GameState
            "equal the original GameState" {
                transformed.toString() shouldBe state.toString()
                transformed shouldBe state

                GameRuleLogic.isFirstMove(transformed) shouldBe true
                transformed.getPointsForPlayer(Team.ONE) shouldBe 0
                transformed.board.isEmpty() shouldBe true
            }
        }
        "cloned" should {
            val cloned = state.clone()
            "preserve equality" {
                 cloned shouldBe state
            }
            "not equal original when lastMoveMono changed" {
                cloned shouldBe state
                cloned.lastMoveMono[Color.RED] = true
                cloned shouldNotBe state
            }
            "not equal original when undeployedPieces changed" {
                cloned shouldBe state
                GameRuleLogic.isFirstMove(cloned) shouldBe true
                cloned.undeployedPieceShapes().remove(cloned.undeployedPieceShapes().first())
                GameRuleLogic.isFirstMove(cloned) shouldBe false
                cloned shouldNotBe state
            }
            "respect validColors" {
                state.removeColor(Color.BLUE)
                val newClone = state.clone()
                newClone shouldBe state
                cloned shouldNotBe state
                newClone.removeColor(Color.BLUE)
                newClone shouldBe state
                newClone.removeColor(Color.RED)
                newClone shouldNotBe state
            }
            val otherState = GameState(lastMove = SetMove(Piece(Color.GREEN, 0)))
            "preserve inequality" {
                otherState shouldNotBe state
                otherState.clone() shouldNotBe state
            }
        }
        "turn number increases" should {
            "let turn, round and currentcolor advance accordingly" {
                GameState().run {
                    turn shouldBe 0
                    round shouldBe 1
                    currentColor shouldBe Color.BLUE

                    turn += 10
                    turn shouldBe 10
                    round shouldBe 3
                    currentColor shouldBe Color.RED

                    turn++
                    turn shouldBe 11
                    round shouldBe 3
                    currentColor shouldBe Color.GREEN

                    turn++
                    turn shouldBe 12
                    round shouldBe 4
                    currentColor shouldBe Color.BLUE
                }
            }
        }
    }
})