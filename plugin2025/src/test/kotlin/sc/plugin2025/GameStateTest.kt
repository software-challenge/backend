package sc.plugin2025

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2025.util.HuIWinReason
import sc.shared.InvalidMoveException
import sc.shared.WinCondition

class GameStateTest: FunSpec({
    test("clone correctly") {
        val state = GameState()
        val clone = state.clone()
        state.currentPlayer.getCards().size shouldBe 0
        clone.currentPlayer.addCard(Card.EAT_SALAD)
        state.currentPlayer.getCards().size shouldBe 0
    }
    val state = GameState(
        Board(arrayOf(Field.START, Field.MARKET, Field.CARROTS, Field.SALAD, Field.HARE, Field.GOAL)),
        lastMove = Advance(5, Card.EAT_SALAD),
        turn = 1,
        players = listOf(
            Hare(Team.TWO, cards = arrayListOf(Card.SWAP_CARROTS), lastAction = Advance(5), carrots = 0, salads = 0),
            Hare(Team.ONE, position = 3, lastAction = Card.EAT_SALAD)
        )
    )
    test("behave properly") {
        state.isOver shouldBe false
        state.currentTeam shouldBe Team.ONE
        state.currentField shouldBe Field.SALAD
        state.currentPlayer.team shouldBe Team.ONE
        state.currentPlayer.lastAction shouldBe Card.EAT_SALAD
        state.mayEatSalad() shouldBe true
        state.currentPlayer.lastAction = EatSalad
        state.mayEatSalad() shouldBe false
    }
    context("allow follow-up Move") {
        state.players.first().clone() shouldBe state.otherPlayer
        state.players.first().position = state.board.size - 1
        state.turn shouldBe 1
        state.isOver shouldBe false
        test("winner") {
            shouldThrow<InvalidMoveException> {
                state.performMoveDirectly(Advance(1))
            }.mistake shouldBe HuIMoveMistake.MUST_EAT_SALAD
            state.turn shouldBe 1
            
            state.performMoveDirectly(EatSalad)
            state.turn shouldBe 2
            state.isOver shouldBe true
            state.winCondition shouldBe WinCondition(Team.TWO, HuIWinReason.GOAL)
        }
        test("tie") {
            state.currentPlayer.run {
                lastAction = EatSalad
                carrots = 13
                salads = 0
            }
            state.performMoveDirectly(Advance(2))
            state.turn shouldBe 2
            state.isOver shouldBe true
            state.winCondition shouldBe null
        }
    }
    test("produce nice XML") {
        Hare(Team.TWO, lastAction = EatSalad) shouldSerializeTo """
              <hare team="TWO" position="0" salads="5" carrots="68">
                <lastAction class="eatsalad"/>
                <cards/>
              </hare>
            """.trimIndent()
        Hare(Team.TWO, cards = arrayListOf(Card.HURRY_AHEAD)) shouldSerializeTo """
              <hare team="TWO" position="0" salads="5" carrots="68">
                <cards>
                  <card>HURRY_AHEAD</card>
                </cards>
              </hare>
            """.trimIndent()
        
        Board(arrayOf(Field.START)) shouldSerializeTo """
                <board>
                  <field>START</field>
                </board>
            """.trimIndent()
        
        state shouldSerializeTo """
              <state startTeam="TWO" turn="1">
                <board>
                  <field>START</field>
                  <field>MARKET</field>
                  <field>CARROTS</field>
                  <field>SALAD</field>
                  <field>HARE</field>
                  <field>GOAL</field>
                </board>
                <hare team="TWO" position="0" salads="0" carrots="0">
                  <lastAction class="advance" distance="5"/>
                  <cards>
                    <card>SWAP_CARROTS</card>
                  </cards>
                </hare>
                <hare team="ONE" position="3" salads="5" carrots="68">
                  <lastAction class="card">EAT_SALAD</lastAction>
                  <cards/>
                </hare>
                <lastMove class="advance" distance="5">
                  <card>EAT_SALAD</card>
                </lastMove>
              </state>
            """.trimIndent()
    }
})
