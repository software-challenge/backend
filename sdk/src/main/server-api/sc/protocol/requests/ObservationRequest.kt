package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Request by administrative client to observe a gameRoom specified by given roomId. */
@XStreamAlias("observe")
data class ObservationRequest(
        @XStreamAsAttribute
        val roomId: String
): AdminLobbyRequest
