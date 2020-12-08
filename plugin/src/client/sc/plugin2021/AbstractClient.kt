package sc.plugin2021

import org.slf4j.LoggerFactory
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.framework.plugins.protocol.MoveRequest
import sc.networking.clients.IControllableGame
import sc.networking.clients.ILobbyClientListener
import sc.networking.clients.LobbyClient
import sc.protocol.responses.PrepareGameProtocolMessage
import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage
import sc.shared.GameResult
import sc.shared.WelcomeMessage
import java.net.ConnectException
import java.util.*
import kotlin.system.exitProcess

/**
 * Eine abstrakte Implementation des [ILobbyClientListener].
 * Hier sind alle Methoden implementiert, die unabhängig von der Logik der Clients der Spieler sind.
 */
abstract class AbstractClient(
        host: String,
        port: Int
): ILobbyClientListener {
    
    companion object {
        private val logger = LoggerFactory.getLogger(AbstractClient::class.java)
        private val plugin: IGamePlugin = ServiceLoader.load(IGamePlugin::class.java).iterator().run {
            if(!hasNext()) throw IllegalStateException("Can't find current game plugin!")
            next()
        }
        private val gameType = plugin.id()
    
        init {
            plugin.initialize()
        }
    }
    
    var isGameOver = false
    
    /** The handler reacts to messages from the server received by the lobby client. */
    protected var handler: IGameHandler? = null
    
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
    
    /** The team the client belongs to. Needed to connect client and player. */
    var team: Team? = null
        private set
    
    /** Tell this client to observe the game given by the preparation handler. */
    fun observeGame(handle: PrepareGameProtocolMessage): IControllableGame =
            client.observe(handle.roomId)
    
    /** Called for any new message sent to the game room, e.g., move requests. */
    override fun onRoomMessage(roomId: String, data: ProtocolMessage) {
        when(data) {
            is MoveRequest -> handler?.onRequestAction()
            is WelcomeMessage -> team = Team.valueOf(data.color.toUpperCase())
        }
        this.roomId = roomId
    }
    
    /** Sends the selected move to the server. */
    fun sendMove(move: Move) =
            client.sendMessageToRoom(roomId, move)
    
    /** Called when an erroneous message is sent to the room. */
    override fun onError(roomId: String, error: ProtocolErrorMessage) {
        logger.debug("onError: Client $this received error ${error.message}")
        this.error = error.message
    }
    
    /**
     * Called when game state has been received.
     * Happens after a client made a move.
     */
    override fun onNewState(roomId: String, state: IGameState) {
        val gameState = state as GameState
        logger.debug("$this got a new state $gameState")
    
        if (team == null || gameState.orderedColors.isEmpty())
            return
    
        if (gameState.currentTeam == team) {
            handler?.onUpdate(gameState.currentPlayer, gameState.otherPlayer)
        } else {
            handler?.onUpdate(gameState.otherPlayer, gameState.currentPlayer)
        }
        handler?.onUpdate(gameState)
    }
    
    /** Start the LobbyClient [client] and listen to it. */
    private fun start() {
        client.start()
        client.addListener(this)
    }
    
    /** [start] and join any game with the appropriate [gameType]. */
    fun joinAnyGame() {
        start()
        client.joinRoomRequest(gameType)
    }
    
    override fun onGameJoined(roomId: String) {}
    override fun onGamePrepared(response: PrepareGameProtocolMessage) {}
    override fun onGamePaused(roomId: String, nextPlayer: Player) {}
    override fun onGameObserved(roomId: String) {}
    
    override fun onGameLeft(roomId: String) {
        logger.info("$this: Got game left in room $roomId")
        client.stop()
    }
    
    override fun onGameOver(roomId: String, data: GameResult) {
        logger.info("$this: Game over with result $data")
        isGameOver = true
        handler?.gameEnded(data, team, error)
    }
    
    fun joinPreparedGame(reservation: String) {
        start()
        client.joinPreparedGame(reservation)
    }
}
