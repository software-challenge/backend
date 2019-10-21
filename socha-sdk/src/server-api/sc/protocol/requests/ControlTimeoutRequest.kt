package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Used to change whether a player in a slot can time out. */
@XStreamAlias("timeout")
data class ControlTimeoutRequest(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val activate: Boolean,
        @XStreamAsAttribute
        val slot: Int
): AdminLobbyRequest
