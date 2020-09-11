package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Response to client who successfully joined a game. */
@XStreamAlias(value = "joined")
data class JoinGameProtocolMessage(
        @XStreamAsAttribute
        val roomId: String
): ProtocolMessage
