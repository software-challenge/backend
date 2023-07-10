package sc.plugin2024.util

import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import sc.plugin2024.Segment

class BoardConverter: Converter {
    override fun canConvert(clazz: Class<*>?): Boolean {
        return clazz == ArrayList::class.java
    }
    
    override fun marshal(value: Any?, writer: HierarchicalStreamWriter?, context: MarshallingContext?) {
        @Suppress("Unchecked_cast") val list = value as ArrayList<Segment>
        
        // only get the last three elements
        val sublist = if(list.size <= 3) list
        else list.subList(list.size - 3, list.size)
        
        sublist.forEach {
            context?.convertAnother(it)
        }
    }
    
    override fun unmarshal(reader: HierarchicalStreamReader?, context: UnmarshallingContext?): Any {
        val list = ArrayList<Segment>()
        while(reader?.hasMoreChildren() == true) {
            reader.moveDown()
            val segment = context?.convertAnother(list, Segment::class.java) as Segment
            list.add(segment)
            reader.moveUp()
        }
        return list
    }
}