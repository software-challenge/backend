package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.protocol.ResponsePacket

/** Response to [sc.protocol.requests.PrepareGameRequest].
 * @param reservations the reservations for the reserved slots */
@XStreamAlias(value = "prepared")
data class GamePreparedResponse(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamImplicit(itemFieldName = "reservation")
        val reservations: List<String>
): ResponsePacket
