package sc.api.plugins

import sc.api.plugins.exceptions.GameLogicException
import sc.api.plugins.host.IGameListener
import sc.framework.plugins.Player
import sc.shared.InvalidMoveException
import sc.shared.PlayerScore

interface IGameInstance {
    /** @return the player that joined. */
    fun onPlayerJoined(): Player

    /**
     * Called by the Server upon receiving a Move.
     *
     * @param fromPlayer The player who invoked this action.
     *
     * @throws GameLogicException   if any invalid action is done
     * @throws InvalidMoveException if the received move violates the rules
     */
    @Throws(GameLogicException::class, InvalidMoveException::class)
    fun onAction(fromPlayer: Player, move: IMove)
    fun addGameListener(listener: IGameListener)
    fun removeGameListener(listener: IGameListener)

    /** Server or an administrator requests the game to start now.  */
    fun start()
    
    /**
     * Stops the Game, removing any open handles.
     *
     * No events (GameOver etc) should be sent out after this method has been called.
     */
    fun stop()
    
    /** Advance the Game by one turn. */
    fun step()
    
    /**
     * Returns the player that has won the game.
     * Null if not finished or no winner.
     */
    val winner: Player?

    /** Used for generating replay name.  */
    val pluginUUID: String

    /** @return the two players, the startplayer will be first in the List
     */
    val players: List<Player>

    /** @return the PlayerScores for both players
     */
    val playerScores: List<PlayerScore>
}
