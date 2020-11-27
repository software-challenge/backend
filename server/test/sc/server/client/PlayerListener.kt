package sc.server.client

import sc.api.plugins.host.IPlayerListener
import sc.protocol.responses.ProtocolMessage

class PlayerListener : IPlayerListener {
    var playerEventReceived = false
    val requests: MutableList<ProtocolMessage> = mutableListOf()

    override fun onPlayerEvent(request: ProtocolMessage) {
        playerEventReceived = true
        requests.add(request)
    }
}