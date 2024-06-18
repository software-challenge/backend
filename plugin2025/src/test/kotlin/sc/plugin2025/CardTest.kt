package sc.plugin2025

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.plugin2025.util.HuIConstants

class CardTest: FunSpec({
    context(Card.SWAP_CARROTS.name) {
        val state = GameState()
        test("allowed only before last salad") {
            Card.SWAP_CARROTS.check(state) shouldBe null
            val lastSalad = state.board.getPreviousField(Field.SALAD)
            lastSalad shouldBe HuIConstants.LAST_SALAD
            state.currentPlayer.position = lastSalad!!
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_BEYOND_LAST_SALAD
            state.currentPlayer.position = lastSalad - 1
            Card.SWAP_CARROTS.check(state) shouldBe null
            state.otherPlayer.position = lastSalad
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_BEYOND_LAST_SALAD
        }
        state.currentPlayer.addCard(Card.SWAP_CARROTS)
        test("not repeatable") {
            // TODO
            //state.performMoveDirectly()
        }
    }
})