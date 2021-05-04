package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ProtocolPacket

/** Response to an erroneous message, including an error [message]. */
@XStreamAlias("errorpacket")
data class ErrorPacket(
        val originalRequest: ProtocolPacket?,
        @XStreamAsAttribute
        val message: String,
): ProtocolPacket {
    val logMessage
        get() = (originalRequest?.let { "$it caused an error:" } ?: "An error occurred:") + message
}
