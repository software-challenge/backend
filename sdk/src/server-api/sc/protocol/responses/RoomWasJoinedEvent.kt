package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ProtocolPacket

/** Sent to all administrative clients after a playerClient joined a game via a JoinRoomRequest. */
@XStreamAlias(value = "joinedGameRoom")
data class RoomWasJoinedEvent(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val existing: Boolean
): ProtocolPacket
