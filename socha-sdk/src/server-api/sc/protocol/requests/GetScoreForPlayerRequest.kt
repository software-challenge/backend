package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/**
 * Request of administrative client to get score for a player (displayName of player).
 * Only useful if testMode was enabled before game ended.
 */
@XStreamAlias("scoreForPlayer")
data class GetScoreForPlayerRequest(
        @XStreamAsAttribute
        val displayName: String
): AdminLobbyRequest
