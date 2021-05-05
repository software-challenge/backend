package sc.api.plugins

import sc.api.plugins.exceptions.GameLogicException
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.api.plugins.host.IGameListener
import sc.framework.plugins.Player
import sc.protocol.room.RoomMessage
import sc.shared.InvalidMoveException
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import kotlin.jvm.Throws

interface IGameInstance {
    /**
     * @return the player that joined
     *
     * @throws TooManyPlayersException when game is already full
     */
    @Throws(TooManyPlayersException::class)
    fun onPlayerJoined(): Player
    fun onPlayerLeft(player: Player, cause: ScoreCause? = null)

    /**
     * Called by the Server once an action was received.
     *
     * @param fromPlayer The player who invoked this action.
     * @param data       ProtocolMessage with the action
     *
     * @throws GameLogicException   if any invalid action is done
     * @throws InvalidMoveException if the received move violates the rules
     */
    @Throws(GameLogicException::class, InvalidMoveException::class)
    fun onAction(fromPlayer: Player, data: RoomMessage)
    fun addGameListener(listener: IGameListener)
    fun removeGameListener(listener: IGameListener)

    /** Server or an administrator requests the game to start now.  */
    fun start()

    /**
     * Destroys the Game.
     * Might be invoked by the server at any time. Any open handles should be removed.
     * No events (GameOver etc) should be sent out after this method has been called.
     */
    fun destroy()

    /**
     * The game is requested to load itself from a file.
     * Similar to a replay but with actual clients.
     *
     * @param file File the game should be loaded from
     */
    fun loadFromFile(file: String)

    /**
     * The game is requested to load itself from a file.
     * Similar to a replay but with actual clients.
     *
     * @param file File where the game should be loaded from
     * @param turn The turn to load from the replay
     */
    fun loadFromFile(file: String, turn: Int)

    /**
     * The game is requested to load itself from a given game information object.
     *
     * @param gameInfo the stored gameInformation
     */
    fun loadGameInfo(gameInfo: Any)

    /**
     * Returns the players that have won the game, empty if the game has no winners,
     * or null if the game has not yet finished.
     */
    val winners: List<Player>

    /** Used for generating replay name.  */
    val pluginUUID: String

    /** @return the two players, the startplayer will be first in the List
     */
    val players: List<Player>

    /** @return the PlayerScores for both players
     */
    val playerScores: List<PlayerScore>
}
