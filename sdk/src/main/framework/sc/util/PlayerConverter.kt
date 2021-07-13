package sc.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.api.plugins.Team
import sc.framework.plugins.Player

class PlayerConverter: Converter {
    override fun canConvert(type: Class<*>): Boolean = type == Player::class.java
    
    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val obj = source as Player
        if(obj.displayName.isNotBlank())
            writer.addAttribute("name", obj.displayName)
        writer.addAttribute("team", obj.team.name)
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext) =
            Player(Team.valueOf(reader.getAttribute("team")), reader.getAttribute("name") ?: "")
}