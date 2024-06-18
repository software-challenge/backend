package sc.plugin2025

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
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
        "produce nice XML" {
            GameState(
                Board(arrayOf(Field.START, Field.MARKET, Field.CARROTS, Field.HARE, Field.GOAL)),
                lastMove = Advance(5, Card.EAT_SALAD)
            ).apply {
                players.forEachIndexed { index, hare ->
                    hare.addCard(Card.values()[index])
                    hare.lastAction = if(index == 0) Card.EAT_SALAD else Advance(index)
                }
            } shouldSerializeTo """
              <state startTeam="ONE" turn="0">
                <board>
                  <field>START</field>
                  <field>MARKET</field>
                  <field>CARROTS</field>
                  <field>HARE</field>
                  <field>GOAL</field>
                </board>
                <player team="ONE" position="0" salads="5" carrots="68">
                  <lastAction class="card">EAT_SALAD</lastAction>
                  <card>FALL_BACK</card>
                </player>
                <player team="TWO" position="0" salads="5" carrots="68">
                  <lastAction class="advance" distance="1"/>
                  <card>HURRY_AHEAD</card>
                </player>
                <lastMove class="advance" distance="5">
                  <card>EAT_SALAD</card>
                </lastMove>
              </state>
            """.trimIndent()
        }
    }
})