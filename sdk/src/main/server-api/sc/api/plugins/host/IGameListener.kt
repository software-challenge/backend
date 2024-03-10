package sc.api.plugins.host

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.shared.GameResult
import sc.shared.PlayerScore

interface IGameListener {
    fun onGameOver(result: GameResult)
    fun onStateChanged(data: IGameState, observersOnly: Boolean)
}