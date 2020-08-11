package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.ITeam
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.plugin2020.util.Constants
import sc.plugin2020.util.GameRuleLogic

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var first: Player = Player(Team.RED),
        override var second: Player = Player(Team.BLUE),
        override var board: Board = Board(),
        turn: Int = 0,
        private val undeployedRedPieces: MutableList<Piece> = parsePiecesString(Constants.STARTING_PIECES, Team.RED),
        private val undeployedBluePieces: MutableList<Piece> = parsePiecesString(Constants.STARTING_PIECES, Team.BLUE),
        override var lastMove: Move? = null
): TwoPlayerGameState(Team.RED) {
    
    override val round: Int
        get() = turn / 2
    
    @XStreamOmitField
    private var allPieces: Collection<Piece> = undeployedBluePieces + undeployedRedPieces + board.getPieces()
    
    @XStreamAsAttribute
    override var turn = turn
        set(value) {
            field = value
            currentTeam = currentPlayerFromTurn() as Team
        }
    
    @XStreamAsAttribute
    override var currentTeam: Team = currentPlayerFromTurn() as Team
        private set
    
    val gameStats: Array<IntArray>
        get() = Team.values().map { getPlayerStats(it) }.toTypedArray()
    
    fun readResolve(): GameState {
        allPieces = undeployedBluePieces + undeployedRedPieces + board.getPieces()
        return this
    }
    
    /** Copy constructor to create a new deeply copied state from the given [state]. */
    constructor(state: GameState): this(state.first.clone(), state.second.clone(), state.board.clone(), state.turn, ArrayList(state.undeployedRedPieces), ArrayList(state.undeployedBluePieces), state.lastMove)
    
    /** Creates a deep copy of this [GameState]. */
    public override fun clone() = GameState(this)
    
    fun getUndeployedPieces(owner: Team): MutableList<Piece> {
        return when(owner) {
            Team.RED -> undeployedRedPieces
            Team.BLUE -> undeployedBluePieces
        }
    }
    
    fun getDeployedPieces(owner: Team): List<Piece> {
        val ownedPieces = allPieces.filterTo(ArrayList()) { it.owner == owner }
        getUndeployedPieces(owner).forEach { ownedPieces.remove(it) }
        return ownedPieces
    }
    
    fun addPlayer(player: Player) {
        when(player.color) {
            Team.RED -> first = player
            Team.BLUE -> second = player
        }
    }
    
    override fun getPointsForPlayer(team: ITeam<*>): Int {
        return GameRuleLogic.freeBeeNeighbours(this.board, team)
    }
    
    fun getPlayerStats(p: Player): IntArray {
        return getPlayerStats(p.color as Team)
    }
    
    fun getPlayerStats(team: Team): IntArray =
            intArrayOf(this.getPointsForPlayer(team))
    
    override fun toString(): String = "GameState Zug $turn"
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is GameState) return false
        
        if(first != other.first) return false
        if(second != other.second) return false
        if(board != other.board) return false
        if(undeployedRedPieces != other.undeployedRedPieces) return false
        if(undeployedBluePieces != other.undeployedBluePieces) return false
        if(allPieces.size != other.allPieces.size || !allPieces.containsAll(other.allPieces)) return false
        if(turn != other.turn) return false
        if(currentTeam != other.currentTeam) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + board.hashCode()
        result = 31 * result + undeployedRedPieces.hashCode()
        result = 31 * result + undeployedBluePieces.hashCode()
        result = 31 * result + allPieces.hashCode()
        result = 31 * result + turn
        return result
    }
    
    companion object {
        fun parsePiecesString(s: String, p: Team): ArrayList<Piece> {
            val l = ArrayList<Piece>()
            for(c in s.toCharArray()) {
                when(c) {
                    'Q' -> l.add(Piece(p, PieceType.BEE))
                    'S' -> l.add(Piece(p, PieceType.SPIDER))
                    'G' -> l.add(Piece(p, PieceType.GRASSHOPPER))
                    'B' -> l.add(Piece(p, PieceType.BEETLE))
                    'A' -> l.add(Piece(p, PieceType.ANT))
                }
            }
            return l
        }
    }
}
