package sc.plugin2021

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.shared.PlayerScore
import sc.shared.ScoreCause

class GameTest: StringSpec({
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
