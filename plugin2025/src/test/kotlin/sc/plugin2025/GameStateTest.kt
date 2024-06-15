package sc.plugin2025

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*

class GameStateTest: WordSpec({
    "GameState" should {
        "clone correctly" {
            val state = GameState()
            val clone = state.clone()
            state.currentPlayer.getCards().size shouldBe 0
            clone.currentPlayer.addCard(Card.EAT_SALAD)
            state.currentPlayer.getCards().size shouldBe 0
        }
    }
})