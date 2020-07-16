package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GameTest: StringSpec({
    "Start a game" {
        val game = Game()
        val player_one = game.onPlayerJoined()
        val player_two = game.onPlayerJoined()
        
        player_one.color shouldBe Team.ONE
        player_two.color shouldBe Team.TWO
    }
})