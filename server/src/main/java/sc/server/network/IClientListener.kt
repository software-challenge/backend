package sc.server.network

import sc.api.plugins.exceptions.RescuableClientException
import sc.networking.clients.XStreamClient.DisconnectCause
import sc.shared.InvalidGameStateException

interface IClientListener {
    /** Invoked when this client disconnected.  */
    fun onClientDisconnected(source: Client, cause: DisconnectCause)
}

interface IClientRequestListener {
    /** Invoked when new data is received and ready to be processed.  */
    @Throws(RescuableClientException::class, InvalidGameStateException::class)
    fun onRequest(source: Client, callback: PacketCallback)
}