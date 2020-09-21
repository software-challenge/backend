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
        println(Color.BLUE)
        println(Color.RED)
        println(Color.YELLOW)
        println(Color.GREEN)
        val game = Game()
        val state = game.gameState
        val player = Pair(game.onPlayerJoined(), game.onPlayerJoined())
        
        player.first.color  shouldBe Team.ONE
        player.second.color shouldBe Team.TWO
        
        game.start()
        
        state.currentPlayer shouldBe player.first
        state.currentColor  shouldBe Color.BLUE
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
    
        state.currentPlayer shouldBe player.second
        state.currentColor  shouldBe Color.YELLOW
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
    
        state.currentPlayer shouldBe player.first
        state.currentColor  shouldBe Color.RED
        game.onAction(player.first, PassMove(Color.RED))
    
        state.currentPlayer shouldBe player.second
        state.currentColor  shouldBe Color.GREEN
        game.onAction(player.second, PassMove(Color.GREEN))
    
        game.winners shouldBe player.toList()
        
        game.playerScores shouldContainExactly List(2)
        {PlayerScore(ScoreCause.REGULAR, "", Constants.DRAW_SCORE, 0)}
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