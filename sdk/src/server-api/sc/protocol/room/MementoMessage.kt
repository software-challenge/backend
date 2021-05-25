package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.IGameState
import sc.framework.plugins.IPerspectiveProvider

/** Sent to update the current state and potentially also a new perspective. */
@XStreamAlias("memento")
data class MementoMessage(
        val state: IGameState,
        @XStreamOmitField private val perspective: Any?
): ObservableRoomMessage, IPerspectiveProvider {
    override fun getPerspective() = perspective
}
