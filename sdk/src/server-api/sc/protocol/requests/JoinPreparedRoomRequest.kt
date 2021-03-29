package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Used by client to join a room by reservation code.
 * The code can be received from the administrative client who requested game creation via PrepareGameRequest.
 */
@XStreamAlias("joinPrepared")
data class JoinPreparedRoomRequest(
        @XStreamAsAttribute
        val reservationCode: String
): ILobbyRequest
