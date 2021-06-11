package sc.networking.clients

import sc.framework.plugins.Player

interface IAdministrativeListener {
    fun onGamePaused(roomId: String, nextPlayer: Player)
}
