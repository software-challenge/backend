package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.plugin2020.util.Constants
import sc.shared.PlayerScore
import sc.shared.ScoreCause

class GameTest: StringSpec({
    "Game starting and stopping works" {
        Color.BLUE.team
        val game = Game()
        val state = game.gameState
        game.onPlayerJoined() shouldBe Team.ONE
        game.onPlayerJoined() shouldBe Team.TWO
        
        game.start()
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        
        game.playerScores shouldBe List(2) { PlayerScore(ScoreCause.REGULAR, "", Constants.DRAW_SCORE, -178) }
    }
})