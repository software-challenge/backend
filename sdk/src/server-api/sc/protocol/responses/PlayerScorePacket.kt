package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.requests.ILobbyRequest
import sc.shared.Score

/** Response to GetScoreForPlayerRequest. */
@XStreamAlias("playerScore")
data class PlayerScorePacket(
        @XStreamAsAttribute
        val score: Score
): ILobbyRequest
