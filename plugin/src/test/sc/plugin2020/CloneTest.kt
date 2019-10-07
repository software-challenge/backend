package sc.plugin2020

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.framework.plugins.Player
import sc.shared.PlayerColor

class CloneTest: StringSpec({
    "clone Player" {
        val player = Player(PlayerColor.RED, "aPlayer")
        player.clone() shouldBe player
    }
    "clone Board" {
        val board = Board()
        board.clone() shouldBe board
    }
    "clone GameState" {
        val state = GameState(blue = Player(PlayerColor.BLUE, "aBluePlayer"), turn = 5)
        val clone = state.clone()
        clone shouldBe state
        clone.getDeployedPieces(PlayerColor.RED) shouldBe state.getDeployedPieces(PlayerColor.RED)
    }
})