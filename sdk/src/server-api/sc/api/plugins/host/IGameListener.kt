package sc.api.plugins.host

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.networking.InvalidScoreDefinitionException
import sc.shared.PlayerScore
import kotlin.jvm.Throws

interface IGameListener {
    @Throws(InvalidScoreDefinitionException::class)
    fun onGameOver(results: Map<Player, PlayerScore>)
    fun onStateChanged(data: IGameState, observersOnly: Boolean)
    fun onPaused(nextPlayer: Player)
}