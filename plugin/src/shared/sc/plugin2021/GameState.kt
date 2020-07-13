package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.shared.Team

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var red: Player = Player(Team.ONE),
        override var blue: Player = Player(Team.TWO),
        override var board: Board = Board(),
        override val turn: Int = 0,
        override var lastMove: Move? = null
): TwoPlayerGameState<Player>() {
    
    @XStreamAsAttribute
    override var currentPlayerColor = Team.ONE
    
    override fun getPointsForPlayer(playerColor: Team): Int = 0
    
}
