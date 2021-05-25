package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

import sc.framework.plugins.Player

/**
 * Indicates to observers that the game has been paused.
 *
 * @param nextPlayer the next Player to move after unpausing.
 */
@XStreamAlias(value = "paused")
data class GamePaused(
        @XStreamAsAttribute
        val nextPlayer: Player
): RoomOrchestrationMessage
