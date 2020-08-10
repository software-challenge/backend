package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.plugin2020.util.Constants
import sc.shared.PlayerScore
import sc.shared.ScoreCause

class GameTest: StringSpec({
    "Start and stop" {
        Color.BLUE.team
        val game = Game()
        val state = game.gameState
        val playerOne = game.onPlayerJoined()
        val playerTwo = game.onPlayerJoined()
        
        playerOne.color shouldBe Team.ONE
        playerTwo.color shouldBe Team.TWO
        game.start()
        
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
        
        game.playerScores shouldBe List(2) { PlayerScore(ScoreCause.REGULAR, "", Constants.DRAW_SCORE, -178) }
    }
})