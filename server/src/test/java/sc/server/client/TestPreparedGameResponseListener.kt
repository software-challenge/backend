package sc.server.client

import sc.networking.clients.AbstractLobbyClientListener
import sc.protocol.responses.GamePreparedResponse

class TestPreparedGameResponseListener : AbstractLobbyClientListener() {
    lateinit var response: GamePreparedResponse

    override fun onGamePrepared(response: GamePreparedResponse) {
        this.response = response
    }
}
