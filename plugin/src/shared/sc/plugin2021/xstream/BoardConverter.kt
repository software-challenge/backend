package sc.plugin2021.xstream

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.plugin2021.util.Constants
import sc.plugin2021.Board
import sc.plugin2021.FieldContent

class BoardConverter: Converter {
    override fun canConvert(type: Class<*>?): Boolean {
        return type == Board::class.java
    }

    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val board = source as Board

        for (y in 0 until Constants.BOARD_SIZE) {
            for (x in 0 until Constants.BOARD_SIZE) {
                if (board[x, y].content != FieldContent.EMPTY) {
                    writer.startNode("field")
                    writer.addAttribute("x", x.toString())
                    writer.addAttribute("y", y.toString())
                    writer.addAttribute("content", board[x, y].content.toString())
                    writer.endNode()
                }
            }
        }
    }

    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        val field: Array<Array<FieldContent>> =
                Array(Constants.BOARD_SIZE) { Array(Constants.BOARD_SIZE) { FieldContent.EMPTY }}

        while (reader.hasMoreChildren()) {
            reader.moveDown()
            val x = reader.getAttribute("x").toInt()
            val y = reader.getAttribute("y").toInt()
            val content = FieldContent.valueOf(reader.getAttribute("content"))
            field[y][x] = content
            reader.moveUp()
        }

        return Board(field)
    }
}