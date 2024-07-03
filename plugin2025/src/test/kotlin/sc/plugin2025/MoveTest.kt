package sc.plugin2025

import io.kotest.assertions.forEachAsClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import sc.helpers.shouldSerializeTo
import sc.protocol.room.RoomPacket
import sc.shared.InvalidMoveException

class MoveTest: WordSpec({
    isolationMode = IsolationMode.InstancePerTest
    "Advance" When {
        val state = GameState(Board(arrayOf(Field.START, Field.MARKET, Field.CARROTS, Field.HARE, Field.GOAL)))
        
        state.checkAdvance(1) shouldBe null
        state.checkAdvance(2) shouldBe null
        state.checkAdvance(3) shouldBe HuIMoveMistake.CARD_NOT_OWNED
        state.checkAdvance(4) shouldBe HuIMoveMistake.GOAL_CONDITIONS
        state.checkAdvance(5) shouldBe HuIMoveMistake.FIELD_NONEXISTENT
        
        state.getSensibleMoves() shouldBe listOf(
            *Card.values().map { Advance(1, it) }.toTypedArray(),
            Advance(2),
        )
        
        state.performMoveDirectly(Advance(2))
        state.turn shouldBe 1
        state.otherPlayer.position shouldBe 2
        
        state.checkAdvance(2) shouldBe HuIMoveMistake.FIELD_OCCUPIED
        state.checkAdvance(3) shouldBe HuIMoveMistake.CARD_NOT_OWNED
        state.checkAdvance(4) shouldBe HuIMoveMistake.GOAL_CONDITIONS
        state.checkAdvance(5) shouldBe HuIMoveMistake.FIELD_NONEXISTENT
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
            
            "allow buy and swap carrots" {
                state.currentPlayer.position shouldBe 0
                state.performMoveDirectly(Advance(1, Card.SWAP_CARROTS))
                state.turn shouldBe 2
                state.currentPlayer.position shouldBe 2
                state.otherPlayer.position shouldBe 1
                state.otherPlayer.getCards() shouldBe listOf(Card.SWAP_CARROTS)
                state.getSensibleMoves(state.otherPlayer) shouldBe listOf(Advance(2, Card.SWAP_CARROTS))
                state.performMoveDirectly(ExchangeCarrots(10))
                state.turn shouldBe 3
                state.currentPlayer.carrots shouldBe 57
                state.otherPlayer.carrots shouldBe 75
                state.performMoveDirectly(Advance(2, Card.SWAP_CARROTS))
                state.turn shouldBe 4
                state.currentPlayer.carrots shouldBe 54
                state.otherPlayer.carrots shouldBe 75
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
                Advance(2, Card.FALL_BACK).perform(state.clone()) shouldBe HuIMoveMistake.FIELD_OCCUPIED
                state.currentPlayer.position++
                Advance(2, Card.FALL_BACK).perform(state.clone()) shouldBe HuIMoveMistake.MUST_BUY_ONE_CARD
                state.clonePlayer { it.position = 1 }.nextCards() shouldBe Card.values().map { listOf(it) }
                state.clonePlayer { it.position = 3 }.nextCards() shouldBe Card.values().map { listOf(Card.FALL_BACK, it) }
                
                state.performMoveDirectly(Advance(2, Card.FALL_BACK, Card.EAT_SALAD))
                state.turn shouldBe 2
                state.otherPlayer.getCards() shouldBe listOf(Card.EAT_SALAD)
            }
        }
        
        "converted to XML" should {
            "be concise" {
                Advance(1) shouldSerializeTo "<advance distance=\"1\"/>"
            }
            "include cards" {
                Advance(3, Card.FALL_BACK, Card.EAT_SALAD) shouldSerializeTo """
                    <advance distance="3">
                      <card>FALL_BACK</card>
                      <card>EAT_SALAD</card>
                    </advance>
                """.trimIndent()
            }
        }
    }
    
    "Fallback" should  {
        val state = GameState(Board(arrayOf(Field.START, Field.CARROTS, Field.HEDGEHOG, Field.CARROTS, Field.HEDGEHOG, Field.HARE)))
        "be invalid on start" {
            state.nextFallBack(state.currentPlayer) shouldBe null
            state.nextFallBack(state.otherPlayer) shouldBe null
        }
        "be invalid before first hedgehog" {
            state.currentPlayer.position = 1
            state.nextFallBack(state.currentPlayer) shouldBe null
        }
        "be invalid on first hedgehog" {
            state.currentPlayer.position = 2
            state.nextFallBack(state.currentPlayer) shouldBe null
        }
        "be valid beyond first hedgehog" {
            state.currentPlayer.position = 3
            state.nextFallBack(state.currentPlayer) shouldBe 2
            state.nextFallBack(state.otherPlayer) shouldBe null
            state.currentPlayer.position = 4
            state.nextFallBack(state.currentPlayer) shouldBe 2
        }
        "be invalid if hedgehog is blocked" {
            state.otherPlayer.position = 4
            state.currentPlayer.position = 5
            state.nextFallBack(state.currentPlayer) shouldBe null
            state.nextFallBack(state.otherPlayer) shouldBe 2
        }
        
        "produce concise XML" {
            FallBack shouldSerializeTo "<fallback/>"
        }
    }
    
    "EatSalad" should {
        val state = GameState(Board(arrayOf(Field.START, Field.CARROTS, Field.HEDGEHOG, Field.HARE, Field.SALAD)))
        "not be valid outside Salad field" {
            (0..3).forEachAsClue {
                state.currentPlayer.position = it
                state.mayEatSalad() shouldBe false
            }
        }
        state.currentPlayer.position = 4
        "be valid on salad field" {
            state.mayEatSalad() shouldBe true
            state.mustEatSalad() shouldBe true
        }
        "be invalid without salad" {
            state.currentPlayer.salads = 0
            state.mayEatSalad() shouldBe false
            state.mustEatSalad() shouldBe true
        }
        "only be accepted once" {
            shouldThrow<InvalidMoveException> {
                state.performMoveDirectly(ExchangeCarrots(10))
            }.mistake shouldBe HuIMoveMistake.MUST_EAT_SALAD
            state.performMoveDirectly(EatSalad)
            state.turn++
            state.mayEatSalad() shouldBe false
            state.mustEatSalad() shouldBe false
            shouldThrow<InvalidMoveException> {
                state.performMoveDirectly(EatSalad)
            }.mistake shouldBe HuIMoveMistake.CANNOT_EAT_SALAD
        }
        
        "produce concise XML" {
            EatSalad shouldSerializeTo "<eatsalad/>"
        }
    }
    
    "ExchangeCarrots" should {
        val state = GameState(Board(arrayOf(Field.START, Field.HEDGEHOG, Field.HARE, Field.SALAD, Field.CARROTS)))
        "not be valid outside Carrot field" {
            (0..3).forEachAsClue {
                state.currentPlayer.position = it
                state.possibleExchangeCarrotMoves().shouldBeEmpty()
            }
        }
        "be valid on carrot field" {
            state.currentPlayer.position = 4
            state.possibleExchangeCarrotMoves() shouldBe listOf(ExchangeCarrots(10), ExchangeCarrots(-10))
        }
        "not produce negative carrots" {
            state.currentPlayer.carrots = 12
            state.performMoveDirectly(Advance(4))
            state.turn++
            state.getSensibleMoves() shouldBe listOf(FallBack, ExchangeCarrots(10))
        }
        
        "produce concicse XML" {
            ExchangeCarrots(-10) shouldSerializeTo "<exchangecarrots amount=\"-10\"/>"
        }
    }
    
    "Move" should {
        "produce a nice room message" {
            RoomPacket("abcd", Advance(3)) shouldSerializeTo """
                <room roomId="abcd">
                  <data class="advance" distance="3"/>
                </room>
            """.trimIndent()
        }
        "serialize Advance properly" {
            Advance(5, Card.EAT_SALAD) shouldSerializeTo """
              <advance distance="5">
                <card>EAT_SALAD</card>
              </advance>
            """.trimIndent()
        }
    }
})
