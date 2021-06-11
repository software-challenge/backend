package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Join a prepared game by reservation.
 * The code is handed out by the administrative client
 * that created the game via a PrepareGameRequest.
 */
@XStreamAlias("joinPrepared")
data class JoinPreparedRoomRequest(
        @XStreamAsAttribute
        val reservationCode: String
): ILobbyRequest
