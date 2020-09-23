package sc.plugin2021.xstream

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.plugin2021.Board
import sc.plugin2021.FieldContent
import sc.plugin2021.util.Configuration

class ConverterTest: StringSpec ({
    "Board conversion" {
        val x = Configuration.xStream
        val board = Board()
        board[0, 0] = FieldContent.RED
        board[1, 3] = FieldContent.GREEN
        board[5, 9] = FieldContent.BLUE
        board[8, 6] = FieldContent.YELLOW

        println(x.toXML(board))
        x.fromXML(x.toXML(board)) shouldBe board
    }
})