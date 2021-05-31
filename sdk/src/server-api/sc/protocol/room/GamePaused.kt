package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Indicates to observers that the game has been (un)paused. */
@XStreamAlias(value = "paused")
data class GamePaused(
        @XStreamAsAttribute
        val paused: Boolean
): ObservableRoomMessage
