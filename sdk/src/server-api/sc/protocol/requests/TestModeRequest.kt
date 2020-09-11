package sc.protocol.requests

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Send by administrative client to enable or disable testMode. */
@XStreamAlias("testMode")
class TestModeRequest(
        @XStreamAsAttribute
        val testMode: Boolean
): AdminLobbyRequest
