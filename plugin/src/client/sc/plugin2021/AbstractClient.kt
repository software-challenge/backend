package sc.plugin2021

import sc.networking.clients.ILobbyClientListener

abstract class AbstractClient: ILobbyClientListener {
    val gameType = GamePlugin.PLUGIN_UUID
}