package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

@XStreamAlias(value = "slotDescriptor")
class SlotDescriptor
@JvmOverloads constructor(
    @XStreamAsAttribute val displayName: String = "Unknown",
    @XStreamAsAttribute val canTimeout: Boolean = true) {
    
    override fun toString(): String = "SlotDescriptor{displayName=$displayName, canTimeout=$canTimeout"
}
