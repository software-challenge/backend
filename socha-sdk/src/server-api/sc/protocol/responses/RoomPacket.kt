package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

import sc.protocol.requests.ILobbyRequest

/** Used to send a [ProtocolMessage] in a room as an [ILobbyRequest]. */
@XStreamAlias("room")
data class RoomPacket(
        @XStreamAsAttribute
        val roomId: String,
        val data: ProtocolMessage
): ILobbyRequest
