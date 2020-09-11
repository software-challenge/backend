package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Used to cancel game and delete a GameRoom. */
@XStreamAlias("cancel")
data class CancelRequest(
        @XStreamAsAttribute
        val roomId: String
): AdminLobbyRequest
