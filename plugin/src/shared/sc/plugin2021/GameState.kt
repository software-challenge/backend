package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IBoard
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.api.plugins.ITeam
import sc.plugin2021.util.GameRuleLogic

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var first: Player = Player(Team.ONE),
        override var second: Player = Player(Team.TWO),
        turn: Int = 0,
        override var lastMove: Move? = null
): TwoPlayerGameState<Player>(Team.ONE) {
    
    override val board: Board = Board()
    
    @XStreamAsAttribute
    val undeployedPieceShapes = mutableMapOf(
            Color.BLUE   to PieceShape.shapes,
            Color.YELLOW to PieceShape.shapes,
            Color.RED    to PieceShape.shapes,
            Color.GREEN  to PieceShape.shapes,
            Color.NONE   to emptyList()
    )
    @XStreamAsAttribute
    val deployedPieces = mutableMapOf(
            Color.BLUE   to ArrayList<Piece>(),
            Color.YELLOW to ArrayList<Piece>(),
            Color.RED    to ArrayList<Piece>(),
            Color.GREEN  to ArrayList<Piece>(),
            Color.NONE   to ArrayList()
    )
    
    @XStreamAsAttribute
    override var currentTeam = currentPlayerFromTurn() as Team
        private set
    
    override var turn: Int = turn
        set(value) {
            field = value
            currentTeam = currentPlayerFromTurn() as Team
        }
    
    override fun getPointsForPlayer(team: ITeam<*>): Int =
            (team as Team).colors.map { getPointsForColor(it) }.sum()
    
    private fun getPointsForColor(color: Color): Int =
            GameRuleLogic.getPointsFromDeployedPieces(deployedPieces[color]!!)
    
}
