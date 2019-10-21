package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Sent by Client to authenticate as administrator. Is not answered if successful. */
@XStreamAlias("authenticate")
data class AuthenticateRequest(
        @XStreamAsAttribute
        val password: String
): ILobbyRequest
