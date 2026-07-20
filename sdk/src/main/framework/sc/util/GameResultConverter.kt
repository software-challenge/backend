package sc.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.api.plugins.Team
import sc.framework.plugins.Player
import sc.shared.*

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

inline fun <reified T> UnmarshallingContext.read() =
    convertAnother(null, T::class.java) as T

class GameResultConverter: Converter {
    override fun canConvert(type: Class<*>?): Boolean = type == GameResult::class.java
    
    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val obj = source as GameResult
        writer.makeNode("definition") { context.convertAnother(obj.definition) }
        writer.makeNode("scores") { context.convertAnother(obj.scores) }
        obj.win?.let { win ->
            val team = win.winner
            writer.makeNode("winner") {
                //addAttribute("team", team?.name.toString())
                team?.name?.let { addAttribute("team", it) }
                addAttribute("regular", win.reason.isRegular.toString())
                val reasonTeam = if(win.reason.isRegular) team else team?.opponent()
                addAttribute("reason", win.reason.getMessage(obj.scores.firstNotNullOfOrNull { entry -> entry.key.displayName.takeIf { it.isNotBlank() && entry.key.team == reasonTeam } } ?: reasonTeam?.name))
            }
        }
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        val definition = reader.readNode { context.read<ScoreDefinition>() }
        val scores = reader.readNode { context.read<LinkedHashMap<Player, PlayerScore>>() }
        val winner =
                if(reader.hasMoreChildren())
                    reader.readNode {
                        WinCondition(
                                getAttribute("team")?.let { attr -> Team.values().find { attr == it.name } },
                                WinReason(
                                        getAttribute("reason"),
                                        getAttribute("regular") == "true"
                                )
                        )
                    }
                else WinCondition(null, WinReasonTie)
        return GameResult(definition, scores, winner)
    }
}