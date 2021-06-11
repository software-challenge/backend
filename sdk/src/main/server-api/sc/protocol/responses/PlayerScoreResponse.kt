package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ResponsePacket
import sc.shared.Score

/** Response to GetScoreForPlayerRequest. */
@XStreamAlias("playerScore")
data class PlayerScoreResponse(
        @XStreamAsAttribute
        val score: Score
): ResponsePacket
