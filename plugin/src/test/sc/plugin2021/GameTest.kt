package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GameTest: StringSpec({
    "Starting and stopping a game" {
        val game = Game()
        val state = game.gameState
        val playerOne = game.onPlayerJoined()
        val playerTwo = game.onPlayerJoined()
        
        playerOne.color shouldBe Team.ONE
        playerTwo.color shouldBe Team.TWO
        
        state.currentPlayer shouldBe playerOne
        state.currentTeam   shouldBe Team.ONE
        state.currentColor  shouldBe Color.BLUE

//        game.start()
        
        game.onAction(state.currentPlayer, PassMove(state.currentColor))
    }
})