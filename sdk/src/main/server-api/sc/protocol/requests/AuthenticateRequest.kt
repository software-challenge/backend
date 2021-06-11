package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Authenticates a client as administrator to send [AdminLobbyRequest]s.
 * Is not answered if successful. */
@XStreamAlias("authenticate")
data class AuthenticateRequest(
        @XStreamAsAttribute
        val password: String
): ILobbyRequest
