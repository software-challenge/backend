package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Sent to all administrative clients after a playerClient joined a game via a JoinRoomRequest. */
@XStreamAlias(value = "joinedGameRoom")
data class GameRoomMessage(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val existing: Boolean
): ProtocolMessage
