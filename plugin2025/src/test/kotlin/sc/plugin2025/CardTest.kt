package sc.plugin2025

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
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
            state.currentPlayer.position = state.board.getPreviousField(Field.HARE, lastSalad)!!
            Card.SWAP_CARROTS.check(state) shouldBe null
            state.currentPlayer.addCard(Card.SWAP_CARROTS)
            state.nextCards() shouldContainExactly listOf(arrayOf(Card.SWAP_CARROTS))
            state.otherPlayer.position = lastSalad
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_BEYOND_LAST_SALAD
            state.nextCards().shouldBeEmpty()
        }
        state.currentPlayer.addCard(Card.SWAP_CARROTS)
        test("not repeatable") {
            Card.SWAP_CARROTS.check(state) shouldBe null
            state.currentPlayer.position = state.board.getNextField(Field.HARE)!! - 1
            state.performMoveDirectly(Advance(1, Card.SWAP_CARROTS))
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_ALREADY_PLAYED
            state.performMoveDirectly(state.getSensibleMoves().first())
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_ALREADY_PLAYED
            state.performMoveDirectly(state.getSensibleMoves().first())
            Card.SWAP_CARROTS.check(state) shouldBe null
        }
    }
})