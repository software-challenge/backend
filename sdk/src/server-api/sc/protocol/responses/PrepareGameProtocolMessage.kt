package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit

/** Response to [sc.protocol.requests.PrepareGameRequest].
 * @param reservations the reservations for the reserved slots */
@XStreamAlias(value = "prepared")
data class PrepareGameProtocolMessage(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamImplicit(itemFieldName = "reservation")
        val reservations: List<String>
): ProtocolMessage
