package sc.plugin2021

import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameRuleLogicTest: StringSpec({
    "Move validation works correctly" {
        val gameState = GameState()
        
        assertThrows<InvalidMoveException> {
            val invalidMove: Move = SetMove(Piece(Color.RED, 3, Rotation.NONE, false))
            GameRuleLogic.validateMove(gameState, invalidMove)
        }
        assertDoesNotThrow {
            val validMove: Move = SetMove(Piece(Color.BLUE, 11, Rotation.NONE, false))
            GameRuleLogic.validateMove(gameState, validMove)
        }
    }
})