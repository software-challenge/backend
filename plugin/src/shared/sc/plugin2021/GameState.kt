package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.plugin2020.Board
import sc.plugin2020.util.GameRuleLogic
import sc.shared.PlayerColor

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var red: Player = Player(PlayerColor.RED),
        override var blue: Player = Player(PlayerColor.BLUE),
        override var board: Board = Board(),
        override val turn: Int = 0,
        override var lastMove: Move? = null
): TwoPlayerGameState<Player>() {
    
    @XStreamAsAttribute
    override var currentPlayerColor = PlayerColor.RED
    
    override fun getPointsForPlayer(playerColor: PlayerColor): Int = 0
    
}
