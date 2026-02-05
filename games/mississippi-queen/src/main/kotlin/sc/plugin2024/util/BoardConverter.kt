package sc.plugin2024.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.api.plugins.CubeDirection
import sc.plugin2024.Board
import sc.plugin2024.Segments
import sc.util.read

class BoardConverter: Converter {
    override fun canConvert(clazz: Class<*>?): Boolean =
            clazz == Board::class.java
    
    override fun marshal(value: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        @Suppress("Unchecked_cast") val board = value as Board
        writer.addAttribute("nextDirection", (board.segments.getOrNull(board.visibleSegments)?.direction ?: board.nextDirection).toString())
        context.convertAnother(board.segments.take(board.visibleSegments))
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Board {
        val dir = CubeDirection.valueOf(reader.getAttribute("nextDirection")) // accept missing value
        val segments = context.read<Segments>()
        return Board(segments, segments.size, nextDirection = dir)
    }
}
