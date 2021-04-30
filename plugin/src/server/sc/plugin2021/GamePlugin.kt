package sc.plugin2021

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.plugin2021.util.Constants
import sc.plugins.PluginDescriptor
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment

@PluginDescriptor(name = "Blokus", uuid = GamePlugin.PLUGIN_ID)
class GamePlugin: IGamePlugin {
    companion object {
        const val PLUGIN_ID = "swc_2021_blokus"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Gewinner"),
                        ScoreFragment("\u2205 Punkte", ScoreAggregation.AVERAGE)
                ))
    }
    
    override val id = PLUGIN_ID
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val gameTimeout: Int =
            Constants.GAME_TIMEOUT
    
    override fun createGame(): IGameInstance =
            Game()
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            Game(state as GameState)
    
}
