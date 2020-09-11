package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.requests.ILobbyRequest

/** Response to TestModeRequest containing the current status of testMode. */
@XStreamAlias(value = "testing")
data class TestModeMessage(
        @XStreamAsAttribute
        val testMode: Boolean
): ILobbyRequest
