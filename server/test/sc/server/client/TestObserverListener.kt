package sc.server.client

import sc.networking.clients.AbstractLobbyClientListener

class TestObserverListener : AbstractLobbyClientListener() {
    var roomid: String? = null

    override fun onGameObserved(roomId: String) {
        roomid = roomId
    }
}