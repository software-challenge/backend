package sc.plugin2025

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo

class GameStateTest: WordSpec({
    "GameState" should {
        "clone correctly" {
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
                Hare(Team.TWO, cards = arrayListOf(Card.SWAP_CARROTS), lastAction = Advance(5)),
                Hare(Team.ONE, position = 3, lastAction = Card.EAT_SALAD)
            )
        )
        "behave properly" {
            state.currentTeam shouldBe Team.ONE
            state.currentField shouldBe Field.SALAD
            state.currentPlayer.team shouldBe Team.ONE
            state.currentPlayer.lastAction shouldBe Card.EAT_SALAD
            state.mayEatSalad() shouldBe true
            state.currentPlayer.lastAction = EatSalad
            state.mayEatSalad() shouldBe false
        }
        "produce nice XML" {
            Hare(Team.TWO, lastAction = EatSalad) shouldSerializeTo """
              <player team="TWO" position="0" salads="5" carrots="68">
                <lastAction class="eatsalad"/>
                <cards/>
              </player>
            """.trimIndent()
            Hare(Team.TWO, cards = arrayListOf(Card.HURRY_AHEAD)) shouldSerializeTo """
              <player team="TWO" position="0" salads="5" carrots="68">
                <cards>
                  <card>HURRY_AHEAD</card>
                </cards>
              </player>
            """.trimIndent()
            
            Advance(5, Card.EAT_SALAD) shouldSerializeTo """
              <advance distance="5">
                <card>EAT_SALAD</card>
              </advance>
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
                <player team="TWO" position="0" salads="5" carrots="68">
                  <lastAction class="advance" distance="5"/>
                  <cards>
                    <card>SWAP_CARROTS</card>
                  </cards>
                </player>
                <player team="ONE" position="3" salads="5" carrots="68">
                  <lastAction class="card">EAT_SALAD</lastAction>
                  <cards/>
                </player>
                <lastMove class="advance" distance="5">
                  <card>EAT_SALAD</card>
                </lastMove>
              </state>
            """.trimIndent()
        }
    }
})