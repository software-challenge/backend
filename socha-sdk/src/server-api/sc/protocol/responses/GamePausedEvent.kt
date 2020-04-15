package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

import sc.framework.plugins.Player

/**
 * Indicates that the game has been paused.
 *
 * @param nextPlayer the Player who will be next
 */
@XStreamAlias(value = "paused")
data class GamePausedEvent(
        @XStreamAsAttribute
        val nextPlayer: Player
): ProtocolMessage
