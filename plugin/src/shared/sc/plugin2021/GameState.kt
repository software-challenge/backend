package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.shared.ITeam

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var red: Player = Player(ITeam.ONE),
        override var blue: Player = Player(ITeam.TWO),
        override var board: Board = Board(),
        override val turn: Int = 0,
        override var lastMove: Move? = null
): TwoPlayerGameState<Player>() {
    
    @XStreamAsAttribute
    override var currentPlayerColor = ITeam.ONE
    
    override fun getPointsForPlayer(playerColor: ITeam): Int = 0
    
}
