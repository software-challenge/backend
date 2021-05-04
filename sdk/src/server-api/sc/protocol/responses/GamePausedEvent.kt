package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

import sc.framework.plugins.Player
import sc.protocol.RoomMessage

/**
 * Indicates that the game has been paused.
 *
 * @param nextPlayer the next Player to move after unpausing
 */
@XStreamAlias(value = "paused")
data class GamePausedEvent(
        @XStreamAsAttribute
        val nextPlayer: Player
): RoomMessage
