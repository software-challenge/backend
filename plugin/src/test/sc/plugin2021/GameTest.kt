package sc.plugin2021

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertThrows
import sc.plugin2020.util.Constants
import sc.shared.InvalidMoveException
import sc.shared.PlayerScore
import sc.shared.ScoreCause

class GameTest: StringSpec({
    "Game starting works" {
        Color.BLUE.team
        val game = Game()
        val state = game.gameState
        val first = game.onPlayerJoined()
        val second = game.onPlayerJoined()
        
        first.color  shouldBe Team.ONE
        second.color shouldBe Team.TWO
        
        game.start()
        
        val e: InvalidMoveException = assertThrows {
            state.currentPlayer shouldBe first
            state.currentColor  shouldBe Color.BLUE
            game.onAction(state.currentPlayer, PassMove(state.currentColor))
        }
    
        println(game.winners)
        game.winners shouldBe listOf(second)
        
        game.playerScores shouldContainExactly listOf(
                PlayerScore(ScoreCause.RULE_VIOLATION, e.message, Constants.LOSE_SCORE, -178),
                PlayerScore(ScoreCause.REGULAR, "", Constants.WIN_SCORE, -178)
        )
    }
})