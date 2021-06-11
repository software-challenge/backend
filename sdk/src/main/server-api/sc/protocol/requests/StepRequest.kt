package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Request by administrative client to send a MoveRequest to the current player.
 * Only works for paused games. */
@XStreamAlias("step")
data class StepRequest @JvmOverloads constructor(
        @XStreamAsAttribute
        var roomId: String,
        @XStreamAsAttribute
        var forced: Boolean = false
): AdminLobbyRequest
