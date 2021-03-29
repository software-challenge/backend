package sc.shared

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

@XStreamAlias(value = "slotDescriptor")
data class SlotDescriptor
@JvmOverloads constructor(
    @XStreamAsAttribute val displayName: String = "Unknown",
    @XStreamAsAttribute val canTimeout: Boolean = true)
