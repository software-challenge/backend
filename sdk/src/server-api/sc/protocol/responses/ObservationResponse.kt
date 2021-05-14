package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ResponsePacket

/** Sent to client as response to successfully joining a GameRoom as Observer. */
@XStreamAlias(value = "observed")
data class ObservationResponse(
        @XStreamAsAttribute
        val roomId: String
): ResponsePacket
