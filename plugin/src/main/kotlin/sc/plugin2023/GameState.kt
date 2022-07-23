package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.*
import sc.api.plugins.Vector.DoubledHex.straight
import sc.plugin2023.util.PenguinMoveMistake
import sc.plugin2023.util.PluginConstants
import sc.shared.MoveMistake
import sc.shared.InvalidMoveException

/**
 * Der aktuelle Spielstand.
 *
 * Er hält alle Informationen zur momentanen Runde,
 * mit deren Hilfe der nächste Zug berechnet werden kann.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
        /** Das aktuelle Spielfeld. */
        override val board: Board = Board(),
        /** Die Anzahl an bereits getätigten Zügen. */
        @XStreamAsAttribute override var turn: Int = 0,
        /** Der zuletzt gespielte Zug. */
        override var lastMove: Move? = null,
        val fishes: IntArray = intArrayOf(Team.values().size),
): TwoPlayerGameState<Move>(Team.ONE) {
    
    constructor(other: GameState): this(other.board.clone(), other.turn, other.lastMove, other.fishes)
    
    override fun performMove(move: Move) {
        if(move.from != null) {
            if(board[move.from].penguin != currentTeam)
                throw InvalidMoveException(MoveMistake.WRONG_COLOR, move)
            if(currentPieces.size < PluginConstants.PENGUINS)
                throw InvalidMoveException(PenguinMoveMistake.PENGUINS, move)
            if(!move.to.minus(move.from).straight)
                throw InvalidMoveException(MoveMistake.INVALID_MOVE, move)
            // TODO avoid this check
            if(move !in getPossibleMoves())
                throw InvalidMoveException(MoveMistake.INVALID_MOVE, move)
            board[move.from] = null
        } else {
            if(currentPieces.size >= PluginConstants.PENGUINS)
                throw InvalidMoveException(PenguinMoveMistake.MAX_PENGUINS, move)
            if(board[move.to].fish != 1)
                throw InvalidMoveException(PenguinMoveMistake.SINGLE_FISH, move)
        }
        fishes[currentTeam.index] += board.set(move.to, currentTeam)
        lastMove = move
        turn++
    }
    
    val currentPieces
        get() = board.filterValues { it.penguin == currentTeam }
    
    override fun getPossibleMoves(): List<Move> {
        val pieces = currentPieces
        return if(pieces.size < PluginConstants.PENGUINS) {
            board.filterValues { it.fish == 1 }.map { Move(null, it.key) }
        } else {
            pieces.flatMap { (pos, _) ->
                // TODO incomplete
                Vector.DoubledHex.directions.map { Move.run(pos, it) }
            }
        }
    }
    
    val isOver: Boolean
        get() = board.filterValues { it.isOccupied }
                .all { it.key.hexNeighbors.all { board[it].fish == 0 } }
    
    /** Berechne die Punkteanzahl für das gegebene Team. */
    override fun getPointsForTeam(team: ITeam): IntArray =
            intArrayOf(fishes[team.index])
    
    override fun clone() = GameState(this)
    
    override fun toString(): String =
            "GameState$turn - ${currentTeam.color} (Fische)"
    
}

val ITeam.color
    get() = if(index == 0) "Rot" else "Blau"
