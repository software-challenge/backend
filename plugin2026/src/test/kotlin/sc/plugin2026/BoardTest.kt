package sc.plugin2026

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.string.*
import io.kotest.matchers.types.*
import sc.helpers.shouldSerializeTo
import sc.networking.XStreamProvider
import sc.plugin2026.util.XStreamClasses
import sc.protocol.LobbyProtocol

class BoardTest: FunSpec({
    val board = Board()
    context("generation") {
        test("obstacles position") {
            board.fieldsEmpty() shouldBe false
        }
    }
    val xstream = XStreamProvider.basic()
    LobbyProtocol.registerAdditionalMessages(xstream, XStreamClasses().classesToRegister)
    context("serialization") {
        test("field") {
            FieldState.ONE_L shouldSerializeTo "<field>ONE_L</field>"
            xstream.fromXML(xstream.toXML(FieldState.ONE_L)) shouldBeSameInstanceAs FieldState.ONE_L
        }
        test("board") {
            xstream.toXML(board) shouldHaveLineCount 122
            
            // FIXME circular reference?
            //val clone = board.clone()
            //clone shouldNotBeSameInstanceAs board
            //clone shouldBe board
            xstream.toXML(board.clone()) shouldHaveLineCount 122
            
            Board(arrayOf(arrayOf(FieldState.ONE_S, FieldState.ONE_M, FieldState.ONE_L))) shouldSerializeTo """
              <board>
                <row>
                  <field>ONE_S</field>
                  <field>ONE_M</field>
                  <field>ONE_L</field>
                </row>
              </board>""".trimIndent()
            
            val xml = xstream.toXML(board)
            val reboard = xstream.fromXML(xml)
            
            (reboard as Board).clone() shouldBe board
        }
    }
})