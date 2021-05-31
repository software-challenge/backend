package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Join a prepared GameRoom without reservation. */
@XStreamAlias("joinRoom")
data class JoinRoomRequest(
        @XStreamAsAttribute
        val roomId: String
): ILobbyRequest