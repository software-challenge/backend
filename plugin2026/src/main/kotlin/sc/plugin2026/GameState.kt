package sc.plugin2019

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.plugin2026.Board
import sc.plugin2026.Move

/**
 * The GameState class represents the current state of the game.
 *
 * It holds all the information about the current round,
 * to provide all information needed to make the next move.
 *
 * @property board The current game board.
 * @property turn The number of turns already made in the game.
 * @property lastMove The last move made in the game.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(Team.ONE) {

    override fun getPointsForTeam(team: ITeam): IntArray =
        GameRuleLogic.greatestSwarmSize(board, playerColor) // TODO important
    
    // TODO implement missing methods - check previous years
    
}