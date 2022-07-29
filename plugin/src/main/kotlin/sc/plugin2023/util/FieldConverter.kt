package sc.plugin2023.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.api.plugins.Team
import sc.plugin2023.Field

class FieldConverter: Converter {
    override fun canConvert(type: Class<*>?): Boolean = type == Field::class.java
    
    override fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val obj = source as Field
        writer.setValue(obj.penguin?.name ?: obj.fish.toString())
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): Field =
            reader.value?.let { value ->
                value.toIntOrNull()?.let { Field(it) } ?: Field(penguin = Team.valueOf(value))
            } ?: Field()
}