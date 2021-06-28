package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias

@XStreamAlias(value = "moveRequest")
class MoveRequest: RoomMessage {
    override fun equals(other: Any?) = other is MoveRequest
    override fun hashCode(): Int = javaClass.hashCode()
}