package sc.plugin2025

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*

class MoveTest: WordSpec({
    "Advance" should {
        "allow cards and buy" {
            val state = GameState(Board(arrayOf(Field.START, Field.MARKET, Field.CARROTS, Field.HARE, Field.GOAL)))
            
            state.checkAdvance(2) shouldBe null
            state.checkAdvance(3) shouldBe MoveMistake.CARD_NOT_OWNED
            state.checkAdvance(4) shouldBe MoveMistake.GOAL_CONDITIONS
            state.checkAdvance(5) shouldBe MoveMistake.FIELD_NONEXISTENT
            
            state.getSensibleMoves().size shouldBe Card.values().size + 1
            
            state.performMoveDirectly(Advance(2))
            state.turn shouldBe 1
            
            state.checkAdvance(2) shouldBe MoveMistake.FIELD_OCCUPIED
            state.checkAdvance(3) shouldBe MoveMistake.CARD_NOT_OWNED
            state.checkAdvance(4) shouldBe MoveMistake.GOAL_CONDITIONS
            state.checkAdvance(5) shouldBe MoveMistake.FIELD_NONEXISTENT
            
            state.currentPlayer.addCard(Card.FALL_BACK)
            state.checkAdvance(3) shouldBe null
            state.performMoveDirectly(Advance(3, Card.FALL_BACK, Card.EAT_SALAD))
            state.currentPlayer.getCards() shouldBe listOf(Card.EAT_SALAD)
        }
    }
})