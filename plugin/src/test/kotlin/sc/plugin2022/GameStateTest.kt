package sc.plugin2022

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.string.*
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2023.*
import java.util.EnumMap

class GameStateTest: FunSpec({
    context("XML Serialization") {
        test("empty state") {
            GameState(Board(listOf(mutableListOf(Field())))) shouldSerializeTo """
                <state turn="0">
                  <startTeam>ONE</startTeam>
                  <board>
                    <list>
                      <field>0</field>
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
            testXStream.toXML(GameState()) shouldHaveLineCount 72
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
})