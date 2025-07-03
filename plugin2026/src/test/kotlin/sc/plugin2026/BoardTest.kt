package sc.plugin2026

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.types.shouldBeSameInstanceAs
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream

class BoardTest: FunSpec({
    val board = Board()
    context("generation") {
        test("obstacles position") {
            board.fieldsEmpty() shouldBe false
        }
    }
    context("serialization") {
        test("board") {
            testXStream.toXML(board) shouldHaveLineCount 122
            
            FieldState.ONE_L shouldSerializeTo "<field>ONE_L</field>"
            testXStream.fromXML(testXStream.toXML(FieldState.ONE_L)) shouldBeSameInstanceAs FieldState.ONE_L
            
            board.clone() shouldBe board
            testXStream.toXML(board.clone()) shouldHaveLineCount 122
            
            Board(arrayOf(arrayOf(FieldState.ONE_S, FieldState.ONE_M, FieldState.ONE_L))) shouldSerializeTo """
              <board>
                <row class="field-array">
                  <field>ONE_S</field>
                  <field>ONE_M</field>
                  <field>ONE_L</field>
                </row>
              </board>""".trimIndent()
            
            val xml = testXStream.toXML(board)
            val reboard = testXStream.fromXML(xml)
            //(reboard as Board).clone() shouldBe board
        }
    }
})