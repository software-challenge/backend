package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.shared.InvalidMoveException

/**
 * The GameState class represents the current state of the game.
 *
 * It holds all the information about the current round, which is used
 * to calculate the next move.
 *
 * @property board The current game board.
 * @property turn The number of turns already made in the game.
 * @property lastMove The last move made in the game.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
    /** Die Anzahl an bereits getätigten Zügen.
     * Modifikation nur via [advanceTurn]. */
    @XStreamAsAttribute override var turn: Int = 0,
    @XStreamImplicit
    val players: List<Hare> = Team.values().map { Hare(it) },
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(players.first().team) {
    
    val currentPlayer
        get() = getHare(currentTeam)
    
    val aheadPlayer
        get() = players.maxByOrNull { it.position }!!
    
    fun getHare(team: ITeam) =
        players.find { it.team == team }!!
    
    val currentField
        get() = board.getField(currentPlayer.position)
    
    /** Das [Team], das am Zug ist. */
    override val currentTeam: Team
        get() = currentTeamFromTurn()
    
    override val isOver: Boolean
        get() = players.any { it.inGoal }
    
    override fun performMoveDirectly(move: Move) {
        move.actions.forEach {
            if(mustPlayCard() && it !is CardAction)
                throw InvalidMoveException(MoveMistake.MUST_PLAY_CARD)
            it.perform(this)
        }
        if(mustPlayCard())
            throw InvalidMoveException(MoveMistake.MUST_PLAY_CARD)
    }
    
    fun mustPlayCard(player: Hare = currentPlayer) =
        currentField == Field.HARE &&
        player.lastAction !is CardAction
    
    fun canEatSalad(player: Hare = currentPlayer) =
        player.salads > 0 &&
        board.getField(player.position) == Field.SALAD &&
        player.lastAction != EatSalad
    
    override fun moveIterator(): Iterator<Move> = TODO()
    
    override fun clone(): GameState =
        copy(board = board.clone(), players = players.clone())
    
    override fun getPointsForTeam(team: ITeam): IntArray =
        getHare(team).let { intArrayOf(it.position, it.salads) }
    
}
