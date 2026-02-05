package sc.plugin2025

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import sc.plugin2025.util.HuIConstants

class CardTest: FunSpec({
    context(Card.SWAP_CARROTS.name) {
        val state = GameState()
        Card.SWAP_CARROTS.check(state) shouldBe null
        state.currentPlayer.addCard(Card.SWAP_CARROTS)
        
        val lastSalad = state.board.getPreviousField(Field.SALAD).shouldNotBeNull()
        lastSalad shouldBe HuIConstants.LAST_SALAD
        
        test("allowed only before last salad") {
            state.currentPlayer.position = lastSalad
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_BEYOND_LAST_SALAD
        }
        test("generate correct moves") {
            val previousHare = state.board.getPreviousField(Field.HARE, lastSalad).shouldNotBeNull()
            state.currentPlayer.position = previousHare - 1
            state.possibleCardMoves(1) shouldContainExactly listOf(Advance(1, Card.SWAP_CARROTS))
            state.currentPlayer.position = previousHare
            Card.SWAP_CARROTS.check(state) shouldBe null
            state.nextCards() shouldContainExactly listOf(arrayOf(Card.SWAP_CARROTS))
            
            val nextHare = state.board.getNextField(Field.HARE, previousHare).shouldNotBeNull()
            state.possibleCardMoves(nextHare - previousHare).shouldBeEmpty()
            
            state.otherPlayer.position = lastSalad
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_BEYOND_LAST_SALAD
            state.nextCards().shouldBeEmpty()
        }
        test("not repeatable") {
            state.currentPlayer.position = state.board.getNextField(Field.HARE).shouldNotBeNull() - 1
            state.performMoveDirectly(Advance(1, Card.SWAP_CARROTS))
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_ALREADY_PLAYED
            state.performMoveDirectly(state.getSensibleMoves().first())
            Card.SWAP_CARROTS.check(state) shouldBe HuIMoveMistake.CANNOT_PLAY_SWAP_CARROTS_ALREADY_PLAYED
            state.performMoveDirectly(state.getSensibleMoves().first())
            Card.SWAP_CARROTS.check(state) shouldBe null
        }
    }
})