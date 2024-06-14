package sc.plugin2023

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.string.*
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2023.util.PenguinConstants
import sc.y

class GameStateTest: FunSpec({
    xcontext("XML Serialization") {
        test("empty state") {
            GameState(makeSimpleBoard(Field(), Field(penguin = Team.TWO))) shouldSerializeTo """
                <state turn="0">
                  <startTeam>ONE</startTeam>
                  <board>
                    <list>
                      <field>0</field>
                      <field>TWO</field>
                    </list>
                  </board>
                  <fishes>
                    <int>0</int>
                    <int>0</int>
                  </fishes>
                </state>
            """.trimIndent()
        }
        test("random state") {
            testXStream.toXML(GameState()) shouldHaveLineCount 89
        }
        test("later state") {
            // TODO
        }
    }
    context("state detection") {
        test("isOver") {
            val state = GameState()
            state.round shouldBe 0
            state.isOver shouldBe false
            val state60 = GameState(turn = 58)
            state60.round shouldBe 29
            state60.isOver shouldBe false
            state60.turn++
            state60.round shouldBe 30
            state60.isOver shouldBe false
            state60.turn++
            state60.round shouldBe 30
            state60.isOver shouldBe false
            //state60.isOver shouldBe true
        }
    }
    context("move calculation") {
        context("initial placement") {
            val emptyBoard = makeBoard()
            GameState(Board()).getSensibleMoves().size shouldBeInRange (PenguinConstants.PENGUINS * 2)..emptyBoard.size
            GameState(emptyBoard).getSensibleMoves() shouldHaveSize emptyBoard.size
        }
        test("first moves") {
            // Board with max penguins for one player
            GameState(makeBoard(*Array(PenguinConstants.PENGUINS) { it y it to 0 })).getSensibleMoves() shouldHaveAtLeastSize PenguinConstants.PENGUINS * 2
        }
        test("immovable") {
            // Board with max penguins for both players
            val state = GameState(Board(arrayOf(
                    Array(PenguinConstants.PENGUINS) { Field(penguin = Team.ONE) },
                    Array(PenguinConstants.PENGUINS) { Field(penguin = Team.TWO) })))
            state.getSensibleMoves().shouldBeEmpty()
            state.board.toString() shouldBe "RRRR\nBBBB"
            state.immovable(Team.ONE).shouldBeTrue()
            state.currentTeam shouldBe Team.TWO
        }
    }
})
