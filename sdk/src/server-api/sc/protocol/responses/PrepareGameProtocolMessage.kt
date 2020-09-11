package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit

/** Response to PrepareGameRequest. */
@XStreamAlias(value = "prepared")
data class PrepareGameProtocolMessage(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamImplicit(itemFieldName = "reservation")
        val reservations: List<String>
): ProtocolMessage
