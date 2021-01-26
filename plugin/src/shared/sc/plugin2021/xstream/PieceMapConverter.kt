package sc.plugin2021.xstream

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.plugin2021.Color
import sc.plugin2021.PieceShape
import java.util.*

class SetConverter: Converter {
    override fun canConvert(type: Class<*>): Boolean =
            Set::class.java.isAssignableFrom(type)
    
    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val set = source as Set<*>
        context.convertAnother(set)
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        val map = EnumMap(Color.values().associateWith {
            PieceShape.values().toMutableSet()
        })
        return map
    }
}

class PieceMapConverter: Converter {
    override fun canConvert(type: Class<*>): Boolean =
            Map::class.java.isAssignableFrom(type)
    
    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        @Suppress("UNCHECKED_CAST")
        val map = source as Map<Color, Collection<PieceShape>>
        map.forEach { (color, collection) ->
            writer.startNode(color.name.toLowerCase() + "Shapes")
            collection.forEach {
                writer.startNode("shape")
                writer.setValue(it.name)
                writer.endNode()
            }
            writer.endNode()
        }
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Any {
        val map = EnumMap<Color, Collection<PieceShape>>(Color::class.java)
        while (reader.hasMoreChildren()) {
            reader.moveDown()
            val color = Color.valueOf(reader.nodeName.removeSuffix("Shapes").toUpperCase())
            val shapes = mutableSetOf<PieceShape>()
            while (reader.hasMoreChildren()) {
                reader.moveDown()
                shapes.add(context.convertAnother(context.currentObject(), PieceShape::class.java) as PieceShape)
                reader.moveUp()
            }
            map[color] = shapes
            reader.moveUp()
        }
        return map
    }
}

