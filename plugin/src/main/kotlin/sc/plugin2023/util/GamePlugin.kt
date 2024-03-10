package sc.plugin2023.util

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.plugin2023.Game
import sc.plugin2023.GameState
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment
import sc.shared.WinReason

class GamePlugin: IGamePlugin {
    companion object {
        const val PLUGIN_ID = "swc_2023_penguins"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen"), ScoreAggregation.SUM),
                        ScoreFragment("Fische", WinReason("%s hat mehr Fische gesammelt"), ScoreAggregation.AVERAGE),
                ))
    }
    
    override val id = PLUGIN_ID
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val turnLimit: Int =
            PluginConstants.BOARD_SIZE * PluginConstants.BOARD_SIZE
    
    override fun createGame(): IGameInstance =
            Game()
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            Game(state as GameState)
    
}
