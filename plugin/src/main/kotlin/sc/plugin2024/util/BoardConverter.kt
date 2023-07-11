package sc.plugin2024.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.plugin2024.SegmentFields

class BoardConverter: Converter {
    override fun canConvert(clazz: Class<*>?): Boolean =
            clazz == ArrayList::class.java
    
    override fun marshal(value: Any?, writer: HierarchicalStreamWriter?, context: MarshallingContext?) {
        @Suppress("Unchecked_cast") val list = value as ArrayList<SegmentFields>
        
        list.takeLast(4).forEach {
            context?.convertAnother(it)
        }
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader?, context: UnmarshallingContext?): Any {
        val list = ArrayList<SegmentFields>()
        while(reader?.hasMoreChildren() == true) {
            reader.moveDown()
            val segment = context?.convertAnother(list, SegmentFields::class.java) as SegmentFields
            list.add(segment)
            reader.moveUp()
        }
        return list
    }
}