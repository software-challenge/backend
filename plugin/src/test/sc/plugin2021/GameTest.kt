package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GameTest: StringSpec({
    "Start a game" {
        val game = Game()
        val playerOne = game.onPlayerJoined()
        val playerTwo = game.onPlayerJoined()
        
        playerOne.color shouldBe Team.ONE
        playerTwo.color shouldBe Team.TWO
        game.start()
    }
})