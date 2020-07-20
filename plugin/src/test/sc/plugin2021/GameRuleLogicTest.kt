package sc.plugin2021

import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameRuleLogicTest: StringSpec({
    "Color validation works correctly" {
        val gameState = GameState()
        gameState.board[Coordinates(1, 1)] = Color.BLUE
        
        assertThrows<InvalidMoveException> {
            val invalidMove: Move = SetMove(Piece(Color.RED, 3, Rotation.NONE, false))
            GameRuleLogic.validateMove(gameState, invalidMove)
        }
        assertDoesNotThrow {
            val validMove: Move = SetMove(
                    Piece(Color.BLUE, 11, Rotation.NONE, false, Coordinates(0, 2)))
            GameRuleLogic.validateMove(gameState, validMove)
        }
    }
    "Position validation works" {
        val gameState: GameState = GameState()
        gameState.board[Coordinates(1, 1)] = Color.BLUE
        
        assertThrows<InvalidMoveException> {
            val invalidMove = SetMove(
                    Piece(Color.BLUE, 0, Rotation.NONE, false, Coordinates(-1, 2)))
            GameRuleLogic.validateMove(gameState, invalidMove)
        }
        GameRuleLogic.isObstructed(gameState.board, Coordinates(1, 1)) shouldBe true
        GameRuleLogic.isObstructed(gameState.board, Coordinates(0, 0)) shouldNotBe true
        
        GameRuleLogic.bordersOnColor(gameState.board, Coordinates(1, 0), Color.BLUE) shouldBe true
        GameRuleLogic.bordersOnColor(gameState.board, Coordinates(0, 0), Color.BLUE) shouldNotBe true
        
        GameRuleLogic.cornersOnColor(gameState.board, Coordinates(0, 0), Color.BLUE) shouldBe true
        GameRuleLogic.cornersOnColor(gameState.board, Coordinates(0, 0), Color.GREEN) shouldNotBe true
        GameRuleLogic.cornersOnColor(gameState.board, Coordinates(1, 0), Color.BLUE) shouldNotBe true
    }
})