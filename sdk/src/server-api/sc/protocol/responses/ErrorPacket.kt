package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ProtocolPacket

/** Response to an erroneous packet, including an error [message]. */
@XStreamAlias("errorpacket")
data class ErrorPacket(
        val originalRequest: ProtocolPacket,
        @XStreamAsAttribute
        val message: String,
): ProtocolPacket
