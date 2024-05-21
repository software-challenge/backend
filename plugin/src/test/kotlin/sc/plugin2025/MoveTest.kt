package sc.plugin2025

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*

class MoveTest: WordSpec({
    isolationMode = IsolationMode.InstancePerTest
    // TODO other move types and card chains
    "Advance" When {
        val state = GameState(Board(arrayOf(Field.START, Field.MARKET, Field.CARROTS, Field.HARE, Field.GOAL)))
        
        state.checkAdvance(1) shouldBe null
        state.checkAdvance(2) shouldBe null
        state.checkAdvance(3) shouldBe MoveMistake.CARD_NOT_OWNED
        state.checkAdvance(4) shouldBe MoveMistake.GOAL_CONDITIONS
        state.checkAdvance(5) shouldBe MoveMistake.FIELD_NONEXISTENT
        
        state.getSensibleMoves() shouldBe listOf(
            *Card.values().map { Advance(1, it) }.toTypedArray(),
            Advance(2),
        )
        
        state.performMoveDirectly(Advance(2))
        state.turn shouldBe 1
        
        state.checkAdvance(2) shouldBe MoveMistake.FIELD_OCCUPIED
        state.checkAdvance(3) shouldBe MoveMistake.CARD_NOT_OWNED
        state.checkAdvance(4) shouldBe MoveMistake.GOAL_CONDITIONS
        state.checkAdvance(5) shouldBe MoveMistake.FIELD_NONEXISTENT
        "one player advanced" should {
            
            "allow eat salad" {
                state.currentPlayer.addCard(Card.EAT_SALAD)
                val adv = Advance(3, Card.EAT_SALAD)
                state.possibleCardMoves(3) shouldContainExactly listOf(adv)
                state.performMoveDirectly(adv)
            }
            
            "allow buy and eat salad" {
                state.currentPlayer.position shouldBe 0
                state.performMoveDirectly(Advance(1, Card.EAT_SALAD))
                state.turn shouldBe 2
                state.currentPlayer.position shouldBe 2
                state.otherPlayer.position shouldBe 1
                state.otherPlayer.getCards() shouldBe listOf(Card.EAT_SALAD)
                state.getSensibleMoves(state.otherPlayer) shouldBe listOf(Advance(2, Card.EAT_SALAD))
                state.performMoveDirectly(ExchangeCarrots(10))
                state.turn shouldBe 3
                state.performMoveDirectly(Advance(2, Card.EAT_SALAD))
                state.turn shouldBe 4
                state.currentPlayer.position shouldBe 2
            }
            
        }
        "has a card" should {
            "allow isolated fallback card" {
                state.currentPlayer.position += 3
                state.currentPlayer.position shouldBe 3
                Card.FALL_BACK.perform(state)
            }
            
            state.currentPlayer.addCard(Card.FALL_BACK)
            state.checkAdvance(3) shouldBe null
            
            "not allow fallback to startfield" {
                state.otherPlayer.position = 1
                state.possibleCardMoves(3).shouldBeEmpty()
            }
            
            "allow fallback and buy" {
                state.checkAdvance(3) shouldBe null
                Advance(3, Card.FALL_BACK).perform(state.clone()) shouldBe MoveMistake.MUST_BUY_ONE_CARD
                state.cloneCurrentPlayer { it.position = 1 }.nextCards() shouldBe Card.values().map { listOf(it) }
                state.cloneCurrentPlayer { it.position = 3 }.nextCards() shouldBe Card.values().map { listOf(Card.FALL_BACK, it) }
                
                state.performMoveDirectly(Advance(3, Card.FALL_BACK, Card.EAT_SALAD))
                state.turn shouldBe 2
                state.otherPlayer.getCards() shouldBe listOf(Card.EAT_SALAD)
            }
        }
    }
})