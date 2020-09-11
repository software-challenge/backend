package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Send by client to join game by gameType.
 * Creates a new gameRoom if no open gameRoom of the specified gameType exists.
 */
@XStreamAlias("join")
data class JoinRoomRequest(
        @XStreamAsAttribute
        val gameType: String
): ILobbyRequest