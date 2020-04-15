package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Response to an erroneous message, including an error [message]. */
@XStreamAlias("error")
data class ProtocolErrorMessage(
        val originalRequest: ProtocolMessage?,
        @XStreamAsAttribute
        val message: String
): ProtocolMessage
