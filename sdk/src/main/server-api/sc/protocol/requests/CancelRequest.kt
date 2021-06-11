package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Deletes the GameRoom and cancels the Game within. */
@XStreamAlias("cancel")
data class CancelRequest(
        @XStreamAsAttribute
        val roomId: String
): AdminLobbyRequest
