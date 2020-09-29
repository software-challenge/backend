package sc.plugin2021.xstream

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import sc.plugin2021.Board
import sc.plugin2021.FieldContent
import sc.plugin2021.GamePlugin.Companion.xStream

/** Test the [BoardConverter]. */
class ConverterTest: StringSpec({
    "Board conversion" {
        val board = Board()
        
        xStream.toXML(board) shouldBe """
            <board/>
        """.trimIndent()
        
        board[0, 0] = FieldContent.RED
        board[1, 3] = FieldContent.GREEN
        board[8, 6] = FieldContent.YELLOW
        board[5, 9] = FieldContent.BLUE
        
        val xml = xStream.toXML(board)
        xStream.fromXML(xml) shouldBe board
        xml shouldBe """
            <board>
              <field x="0" y="0" content="RED"/>
              <field x="1" y="3" content="GREEN"/>
              <field x="8" y="6" content="YELLOW"/>
              <field x="5" y="9" content="BLUE"/>
            </board>
        """.trimIndent()
    }
})