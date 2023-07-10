package sc.plugin2024.util

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.plugin2024.Game
import sc.plugin2024.GameState
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment

class GamePlugin: IGamePlugin {
    companion object {
        const val PLUGIN_ID = "swc_2024_mississippi_queen"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", ScoreAggregation.SUM),
                        ScoreFragment("Punkte", ScoreAggregation.AVERAGE),
                        ScoreFragment("Passagiere", ScoreAggregation.AVERAGE),
                ))
    }
    
    override val id = PLUGIN_ID
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val turnLimit: Int =
            PluginConstants.ROUND_LIMIT * 2
    
    override fun createGame(): IGameInstance =
            Game()
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            Game(state as GameState)
    
}
