package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.*
import sc.api.plugins.Coordinates
import sc.api.plugins.HexDirection
import sc.plugin2023.util.PenguinsMoveMistake
import sc.plugin2023.util.PenguinConstants
import sc.shared.InvalidMoveException
import sc.shared.MoveMistake
import sc.shared.WinCondition

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
        val fishes: IntArray = IntArray(Team.values().size),
): TwoPlayerGameState<Move>(Team.ONE) {

    constructor(other: GameState): this(other.board.clone(), other.turn, other.lastMove, other.fishes.clone())
    
    override val currentTeam: Team
        get() = currentTeamFromTurn().run { takeIf { !immovable(it) } ?: opponent() }
    
    override fun performMoveDirectly(move: Move) {
        if(move.from != null) {
            if(board[move.from].penguin != currentTeam)
                throw InvalidMoveException(MoveMistake.WRONG_COLOR, move)
            if(currentPieces.size < PenguinConstants.PENGUINS)
                throw InvalidMoveException(PenguinsMoveMistake.PLACE_PENGUINS_FIRST, move)
            if(!move.to.minus(move.from).straightHex)
                throw InvalidMoveException(MoveMistake.INVALID_MOVE, move)
            // TODO avoid this check
            if(move !in board.possibleMovesFrom(move.from))
                throw InvalidMoveException(MoveMistake.INVALID_MOVE, move)
            board[move.from] = null
        } else {
            if(currentPieces.size >= PenguinConstants.PENGUINS)
                throw InvalidMoveException(PenguinsMoveMistake.MAX_PENGUINS, move)
            if(board[move.to].fish != 1)
                throw InvalidMoveException(PenguinsMoveMistake.SINGLE_FISH, move)
        }
        fishes[currentTeam.index] += board.set(move.to, currentTeam)
        lastMove = move
        turn++
    }
    
    val currentPieces
        get() = board.filterValues { it.penguin == currentTeam }
    
    val penguinsPlaced
        get() = currentPieces.size == PenguinConstants.PENGUINS
    
    override fun getSensibleMoves(): List<Move> =
            if(penguinsPlaced) {
                currentPieces.flatMap { (pos, _) -> board.possibleMovesFrom(pos) }
            } else {
                board.filterValues { it.fish == 1 }.map { Move(null, it.key) }
            }
    
    override fun moveIterator(): Iterator<Move> =
            getSensibleMoves().iterator()
    
    fun canPlacePenguin(pos: Coordinates) =
        !penguinsPlaced && board[pos].fish == 1
    
    fun immovable(team: Team? = null) =
        board.getPenguins()
                    .filter { team == null || it.second == team }
                    .takeIf { it.size == PenguinConstants.PENGUINS * (if(team == null) Team.values().size else 1) }
                    ?.all { pair -> HexDirection.values().map { pair.first + it }.all { board.getOrEmpty(it).fish == 0 } } ?: false
    
    override val isOver: Boolean
        get() = immovable()
    
    override val winCondition: WinCondition?
        get() = TODO("Not yet implemented")
    
    /** Berechne die Punkteanzahl für das gegebene Team. */
    override fun getPointsForTeam(team: ITeam): IntArray =
            intArrayOf(fishes[team.index])
    
    override fun teamStats(team: ITeam) = listOf<Stat>()
    
    override fun clone() = GameState(this)
    
    override fun toString(): String =
            "GameState$turn - ${currentTeam.color} (Fische)"
    
    // Generated Stuff below
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is GameState) return false
        
        if(board != other.board) return false
        if(turn != other.turn) return false
        if(lastMove != other.lastMove) return false
        if(!fishes.contentEquals(other.fishes)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = board.hashCode()
        result = 31 * result + turn
        result = 31 * result + (lastMove?.hashCode() ?: 0)
        result = 31 * result + fishes.contentHashCode()
        return result
    }
    
}
