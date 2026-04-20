package sc.plugin2099

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.types.shouldBeSameInstanceAs
import sc.helpers.shouldSerializeTo
import sc.networking.XStreamProvider
import sc.plugin2099.util.XStreamClasses
import sc.protocol.LobbyProtocol

class BoardTest: FunSpec( {
    val board = Board()
    context("generation") {
        test("obstacles position") {
            board.fieldsEmpty() shouldBe true
        }
    }

    val xstream = XStreamProvider.basic()
    LobbyProtocol.registerAdditionalMessages(xstream, XStreamClasses().classesToRegister)
    context("serialization") {
        test("field") {
            FieldState.CIRCLE shouldSerializeTo "<field>CIRCLE</field>"
            xstream.fromXML(xstream.toXML(FieldState.CIRCLE)) shouldBeSameInstanceAs FieldState.CIRCLE
        }

        test("board") {
            xstream.toXML(board) shouldHaveLineCount 17
            xstream.toXML(board.clone()) shouldHaveLineCount 17

            Board(arrayOf(arrayOf(FieldState.CIRCLE, FieldState.CROSS))) shouldSerializeTo """
              <board>
                <row>
                  <field>CIRCLE</field>
                  <field>CROSS</field>
                </row>
              </board>""".trimIndent()

            val xml = xstream.toXML(board)
            val reboard = xstream.fromXML(xml)

            (reboard as Board).clone() shouldBe board
        }
    }
})