package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ProtocolPacket

/** Sent to all administrative clients when a player joined a game via a JoinRoomRequest.
 * @param existing whether the joined room has existed beforehand or was newly created. */
@XStreamAlias(value = "joinedGameRoom")
data class RoomWasJoinedEvent(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val existing: Boolean
): ProtocolPacket
