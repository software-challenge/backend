package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.ProtocolPacket

/** Used to send a [RoomMessage] to a room. */
@XStreamAlias("room")
data class RoomPacket(
        @XStreamAsAttribute
        val roomId: String,
        val data: RoomMessage,
): ProtocolPacket
