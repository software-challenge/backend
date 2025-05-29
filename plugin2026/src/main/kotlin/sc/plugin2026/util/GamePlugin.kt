package sc.plugin2026.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.TwoPlayerGame
import sc.plugin2026.GameState
import sc.plugin2026.Move
import sc.shared.*

@XStreamAlias(value = "winreason")
enum class PiranhasWinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    SOLE_SWARM("%s hat als einziger einen zusammenhängenden Schwarm"),
    BIGGER_SWARM("%s hat den größeren Schwarm"),
}

class GamePlugin: IGamePlugin<Move> {
    companion object {
        const val PLUGIN_ID = "swc_2026_piranhas"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen."), ScoreAggregation.SUM),
                        ScoreFragment("Schwarmgröße", PiranhasWinReason.BIGGER_SWARM, ScoreAggregation.AVERAGE),
                ))
    }
    
    override val id = PLUGIN_ID
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val turnLimit: Int =
        PiranhaConstants.ROUND_LIMIT * 2
    
    override val moveClass = Move::class.java
    
    override fun createGame(): IGameInstance =
            TwoPlayerGame(this, GameState())
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            TwoPlayerGame(this, state as GameState)
    
}
