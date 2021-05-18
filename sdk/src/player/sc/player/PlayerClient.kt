package sc.player

import org.slf4j.LoggerFactory
import sc.api.plugins.*
import sc.framework.plugins.protocol.MoveRequest
import sc.networking.clients.AbstractLobbyClientListener
import sc.networking.clients.LobbyClient
import sc.protocol.room.ErrorMessage
import sc.protocol.room.RoomMessage
import sc.shared.GameResult
import java.net.ConnectException
import kotlin.system.exitProcess

/**
 * Eine Implementation des [AbstractLobbyClientListener],
 * um die Server-Kommunikation mit der Logik der Spieler zu verbinden.
 */
class PlayerClient(
        host: String,
        port: Int,
        private val handler: IGameHandler,
): AbstractLobbyClientListener() {
    companion object {
        private val logger = LoggerFactory.getLogger(PlayerClient::class.java)
    }
    
    var isGameOver = false
    
    /** The lobby client that connects to the room. Stops on connection failure. */
    private val client: LobbyClient = try {
        LobbyClient(host, port)
    } catch (e: ConnectException) {
        logger.error("Could not connect to Server: ${e.message}")
        exitProcess(1)
    }
    
    /** Storage for the reason of a rule violation, if any occurs. */
    var error: String? = null
        private set
    
    /** Current room of the player. */
    private lateinit var roomId: String
    
    /** Called for any new message sent to the game room, e.g., move requests. */
    override fun onRoomMessage(roomId: String, data: RoomMessage) {
        this.roomId = roomId
        when (data) {
            is MoveRequest -> sendMove(handler.calculateMove())
            is ErrorMessage -> {
                logger.debug("onError: Client $this received error ${data.message} in $roomId")
                this.error = data.message
            }
        }
    }
    
    /** Sends the selected move to the server. */
    fun sendMove(move: IMove) =
            client.sendMessageToRoom(roomId, move)
    
    /**
     * Called when game state has been received.
     * Happens after a client made a move.
     */
    override fun onNewState(roomId: String, state: IGameState) {
        val gameState = state as TwoPlayerGameState
        logger.debug("$this got a new state $gameState")
        handler.onUpdate(gameState)
    }
    
    /** Start the LobbyClient [client] and listen to it. */
    private fun start() {
        client.start()
        client.addListener(this)
    }
    
    /** [start] and join any game with the appropriate [gameType]. */
    fun joinAnyGame() {
        start()
        client.joinRoomRequest(IGamePlugin.loadPluginId())
    }
    
    override fun onGameLeft(roomId: String) {
        logger.info("$this: Got game left in room $roomId")
        client.stop()
    }
    
    override fun onGameOver(roomId: String, data: GameResult) {
        logger.info("$this: Game over with result $data")
        isGameOver = true
        handler.onGameOver(data, error)
    }
    
    fun joinPreparedGame(reservation: String) {
        start()
        client.joinPreparedGame(reservation)
    }
}
