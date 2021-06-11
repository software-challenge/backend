package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Request by administrative client to toggle testMode. */
@XStreamAlias("testMode")
class TestModeRequest(
        @XStreamAsAttribute
        val testMode: Boolean
): AdminLobbyRequest
