package sc.plugin2021

import org.slf4j.LoggerFactory
import sc.framework.plugins.Player
import sc.framework.plugins.protocol.MoveRequest
import sc.networking.clients.IControllableGame
import sc.networking.clients.ILobbyClientListener
import sc.networking.clients.LobbyClient
import sc.plugin2021.util.Configuration
import sc.protocol.responses.PrepareGameProtocolMessage
import sc.protocol.responses.ProtocolErrorMessage
import sc.shared.GameResult
import sc.shared.WelcomeMessage
import java.io.IOException
import java.net.ConnectException
import kotlin.system.exitProcess

/** Abstract client following the SDK.
 * Contains a LobbyClient as actual client;
 * this class is a plugin specific abstract wrapper.
 */
abstract class AbstractClient @Throws(IOException::class) constructor(
        host: String,
        port: Int,
        private val id: PlayerType = PlayerType.PLAYER_ONE
): ILobbyClientListener {
    
    companion object {
        private val logger = LoggerFactory.getLogger(AbstractClient::class.java);
        private val gameType = GamePlugin.PLUGIN_UUID
    }
    
    /** The handler reacts to messages from the server received by the lobby client.
     *  It *must* be initialised before start.
     */
    protected lateinit var handler: IGameHandler
    
    /** Initialise game handler. */
    fun setGameHandler(handler: IGameHandler) {
        this.handler = handler
    }
    
    /** The lobby client that connects to the room. Stops on connection failure. */
    private val client = try {
        LobbyClient(Configuration.xStream, Configuration.classesToRegister, host, port)
    } catch(e: ConnectException) {
        logger.error("Could not connect to Server: " + e.message)
        exitProcess(1)
    }
    
    /** Storage for the reason of a rule violation, if any occurs. */
    private lateinit var error: String
    fun getError() = error
    
    private lateinit var roomID: String
    
    /** The team the client belongs to in order to connect client and player. */
    private var team: Team? = when(id) {
            PlayerType.PLAYER_ONE -> Team.ONE
            PlayerType.PLAYER_TWO -> Team.TWO
            else -> null
    }
    
    /** Tell this client to observe the game given by the preparation handler.
     *
     * @return controllable game
     */
    fun observeGame(handle: PrepareGameProtocolMessage): IControllableGame =
            client.observe(handle)
    
    /** Called for any new message sent to the game room, e.g., move requests. */
    override fun onRoomMessage(roomId: String, data: Any) {
        if(data is MoveRequest) {
            handler.onRequestAction()
        }
        if(data is WelcomeMessage) {
            team = data.playerColor as Team
        }
        roomID = roomId
    }
    
    /** Sends the selected move to the server. */
    fun sendMove(move: Move) =
            client.sendMessageToRoom(roomID, move)
    
    /** Called when an erroneous message is sent to the room. */
    override fun onError(roomId: String, error: ProtocolErrorMessage) {
        logger.debug("onError: Client {} received error {}", this, error.message)
        this.error = error.message
    }
    
    override fun onNewState(roomId: String, state: Any) {
        val gameState = state as GameState
        logger.debug("{} got a new state {}", this, gameState)
        
        if(id == PlayerType.OBSERVER) return
        
        handler.onUpdate(gameState)
        if(gameState.currentTeam == team) {
            handler.onUpdate(gameState.currentPlayer, gameState.otherPlayer)
        } else {
            handler.onUpdate(gameState.otherPlayer, gameState.currentPlayer)
        }
    }
    
    fun joinAnyGame() =
            client.joinRoomRequest(gameType)
    
    override fun onGameJoined(roomId: String) {}
    override fun onGamePrepared(response: PrepareGameProtocolMessage) {}
    override fun onGamePaused(roomId: String, nextPlayer: Player) {}
    override fun onGameObserved(roomId: String) {}
    
    override fun onGameLeft(roomId: String) {
        logger.info("{} got game left {}", this, roomId)
        client.stop()
    }
    
    override fun onGameOver(roomId: String, data: GameResult) {
        logger.info("{} on Game Over with game result {}", this, data)
        if (this::handler.isInitialized) {
            handler.gameEnded(data, team, error.orEmpty())
        }
    }
    
    fun joinPreparedGame(reservation: String) =
            client.joinPreparedGame(reservation)
}