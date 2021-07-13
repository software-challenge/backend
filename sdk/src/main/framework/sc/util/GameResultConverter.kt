package sc.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.api.plugins.Team
import sc.framework.plugins.Player
import sc.shared.GameResult
import sc.shared.PlayerScore
import sc.shared.ScoreDefinition

fun HierarchicalStreamWriter.makeNode(node: String, action: HierarchicalStreamWriter.() -> Unit) {
    startNode(node)
    action()
    endNode()
}

fun <T> HierarchicalStreamReader.readNode(action: HierarchicalStreamReader.() -> T): T {
    moveDown()
    @Suppress("UNCHECKED_CAST") val result = action()
    moveUp()
    return result
}

inline fun <reified T> UnmarshallingContext.read() = convertAnother(null, T::class.java) as T

class GameResultConverter: Converter {
    override fun canConvert(type: Class<*>?): Boolean = type == GameResult::class.java
    
    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val obj = source as GameResult
        writer.makeNode("definition") { context.convertAnother(obj.definition) }
        writer.makeNode("scores") { context.convertAnother(obj.scores) }
        obj.winner?.let { writer.makeNode("winner") { addAttribute("team", it.name) } }
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        val definition = reader.readNode { context.read<ScoreDefinition>() }
        val scores = reader.readNode { context.read<LinkedHashMap<Player, PlayerScore>>() }
        return GameResult(definition, scores, if(reader.hasMoreChildren()) reader.readNode { Team.valueOf(getAttribute("team")) } else null)
    }
}