package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Join a game by [gameType].
 * Creates a new gameRoom if no open gameRoom of the specified gameType exists.
 */
@XStreamAlias("join")
data class JoinGameRequest(
        @XStreamAsAttribute
        val gameType: String?
): ILobbyRequest