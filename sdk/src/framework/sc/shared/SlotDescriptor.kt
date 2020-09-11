package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

@XStreamAlias(value = "slotDescriptor")
class SlotDescriptor(
        @XStreamAsAttribute val displayName: String,
        @XStreamAsAttribute val canTimeout: Boolean,
        @XStreamAsAttribute val shouldBePaused: Boolean) {
    
    constructor(displayName: String, canTimeout: Boolean) : this(displayName, canTimeout, true)
    constructor(displayName: String) : this(displayName, true, true)
    constructor() : this("Unknown", true, true)
    
    override fun toString(): String {
        return "SlotDescriptor{displayName=$displayName, canTimeout=$canTimeout, shouldBePaused=$shouldBePaused"
    }
}