package sc.plugin2027.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.api.plugins.Coordinates
import sc.plugin2027.Board
import sc.plugin2027.Field
import sc.plugin2027.FieldContent

/**
 * XStream converter class to make the XML look nicer.
 * With this converter, the board has the format with omitted empty fields:
 * <board>
 *     <field x="0" y="0" content="RED"/>
 *     ...
 * </board>
 * instead of a nested array with all elements, which would look like this:
 * <board>
 *     <row>
 *         <field content="RED"/>
 *         <field content="EMPTY"/>
 *         <field content="EMPTY"/>
 *         ...
 *     </row>
 *     ...
 * </board>
 */
class BoardConverter: Converter {

    override fun canConvert(type: Class<*>?): Boolean =
        type == Board::class.java

    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val board = source as Board

        for (y in 0 until Constants.BOARD_LENGTH) {
            for (x in 0 until Constants.BOARD_LENGTH) {
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
        // Initialize an empty board
        val fields = Array(Constants.BOARD_LENGTH) { y ->
            Array(Constants.BOARD_LENGTH) { x ->
                Field(Coordinates(x, y), FieldContent.EMPTY)
            }
        }
        
        // Read the fields from the XML and fill the board
        while (reader.hasMoreChildren()) {
            reader.moveDown()
            try {
                val x = reader.getAttribute("x").toInt()
                val y = reader.getAttribute("y").toInt()
                val content = FieldContent.valueOf(reader.getAttribute("content"))
                // FIXME why y und x and not x und y?
                fields[y][x] = Field(Coordinates(x, y), content)
            } catch (e: NullPointerException) {
                throw NullPointerException("Missing attribute in field node: ${e.message}")
            }
            reader.moveUp()
        }

        return Board(fields)
    }
}