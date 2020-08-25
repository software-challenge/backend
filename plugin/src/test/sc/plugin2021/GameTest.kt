package sc.plugin2021

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertThrows
import sc.plugin2020.util.Constants
import sc.plugin2021.util.GameRuleLogic
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
    
        game.winners shouldBe listOf(second)
        
        game.playerScores shouldContainExactly listOf(
                PlayerScore(ScoreCause.RULE_VIOLATION, e.message, Constants.LOSE_SCORE, -178),
                PlayerScore(ScoreCause.REGULAR, "", Constants.WIN_SCORE, -178)
        )
    }
    "A few moves can be performd without issues" {
        val game = Game()
        val state = game.gameState
        Pair(game.onPlayerJoined(), game.onPlayerJoined())
        game.start()
        
        while (true) {
            try {
                val condition = game.checkWinCondition()
                if (condition != null) {
                    println(condition)
                    break
                }
                val moves = GameRuleLogic.getPossibleMoves(state)
                moves shouldNotBe emptySet<SetMove>()
                game.onAction(state.currentPlayer, moves.random())
            } catch (e: Exception) {
                println(e.message)
                break
            }
        }
    }
})