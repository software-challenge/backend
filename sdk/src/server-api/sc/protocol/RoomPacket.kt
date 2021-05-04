package sc.protocol

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.RoomMessage

import sc.protocol.requests.ILobbyRequest

/** Used to send a [RoomMessage] in a room as an [ILobbyRequest]. */
@XStreamAlias("room")
data class RoomPacket(
        @XStreamAsAttribute
        val roomId: String,
        val data: RoomMessage
): ILobbyRequest
