package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Frees a claimed reservation. */
@XStreamAlias("freeReservation")
data class FreeReservationRequest(
        @XStreamAsAttribute
        val reservation: String
): AdminLobbyRequest
