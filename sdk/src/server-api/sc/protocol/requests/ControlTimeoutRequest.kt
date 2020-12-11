package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Used to change whether a player in a slot can time out. */
// TODO this shouldn't need changing after a game was started
// instead, a game should be preparable without reservations
@XStreamAlias("timeout")
data class ControlTimeoutRequest(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val activate: Boolean,
        @XStreamAsAttribute
        val slot: Int
): AdminLobbyRequest
