package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ResponsePacket

/** Sent to all administrative clients after a player joined a GameRoom via a JoinRoomRequest.
 * @param playerCount the number of players in the room after the join. */
@XStreamAlias(value = "joinedGameRoom")
data class RoomWasJoinedEvent(
        @XStreamAsAttribute
        val roomId: String,
        @XStreamAsAttribute
        val playerCount: Int
): ResponsePacket
