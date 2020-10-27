package sc.plugin2021

import org.slf4j.LoggerFactory
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
import java.io.IOException
import java.net.ConnectException
import kotlin.system.exitProcess

/**
 * Eine abstrakte Implementation des [ILobbyClientListener].
 * Hier sind alle Methoden implementiert, die unabhängig von der Logik der Clients der Spieler sind.
 */
abstract class AbstractClient @Throws(IOException::class) constructor(
        host: String,
        port: Int,
        private val type: PlayerType
): ILobbyClientListener {
    
    constructor(host: String, port: Int): this(host, port, PlayerType.PLAYER_ONE) {}

    companion object {
        private val logger = LoggerFactory.getLogger(AbstractClient::class.java);
        private val gameType = GamePlugin.PLUGIN_UUID
    
        init {
            GamePlugin.registerXStream()
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
    private var error: String? = null
    fun getError() = error
    
    /** Current room of the player. */
    private lateinit var roomId: String
    
    /** The team the client belongs to in order to connect client and player. */
    private var team: Team? = when (type) {
        PlayerType.PLAYER_ONE -> Team.ONE
        PlayerType.PLAYER_TWO -> Team.TWO
        else -> null
    }
    
    /** Tell this client to observe the game given by the preparation handler. */
    fun observeGame(handle: PrepareGameProtocolMessage): IControllableGame =
            client.observe(handle)
    
    /** Called for any new message sent to the game room, e.g., move requests. */
    override fun onRoomMessage(roomId: String, data: ProtocolMessage) {
        if (data is MoveRequest) {
            handler?.onRequestAction()
        }
        if (data is WelcomeMessage) {
            team = Team.valueOf(data.color.toUpperCase())
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
        
        if (type == PlayerType.OBSERVER) return
        
        if (gameState.orderedColors.isNotEmpty()) {
            if (gameState.currentTeam == team) {
                handler?.onUpdate(gameState.currentPlayer, gameState.otherPlayer)
            } else {
                handler?.onUpdate(gameState.otherPlayer, gameState.currentPlayer)
            }
            handler?.onUpdate(gameState)
        }
    }
    
    private fun start() {
        client.start()
        client.addListener(this)
    }
    
    fun joinAnyGame() {
        start()
        client.joinRoomRequest(gameType)
    }
    
    override fun onGameJoined(roomId: String) {}
    override fun onGamePrepared(response: PrepareGameProtocolMessage) {}
    override fun onGamePaused(roomId: String, nextPlayer: Player) {}
    override fun onGameObserved(roomId: String) {}
    
    override fun onGameLeft(roomId: String) {
        logger.info("$this got game left ${this.roomId}")
        client.stop()
    }
    
    override fun onGameOver(roomId: String, data: GameResult) {
        logger.info("$this on Game Over with game result $data")
        isGameOver = true
        handler?.gameEnded(data, team, error)
    }
    
    fun joinPreparedGame(reservation: String) {
        start()
        client.joinPreparedGame(reservation)
    }
}