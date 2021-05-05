package sc.server.client

import sc.api.plugins.host.IPlayerListener
import sc.protocol.room.RoomMessage

class PlayerListener : MessageListener<RoomMessage>(), IPlayerListener {
    override fun onPlayerEvent(request: RoomMessage) {
        addMessage(request)
    }
}