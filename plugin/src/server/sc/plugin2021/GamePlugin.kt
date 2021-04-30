package sc.plugin2021

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.api.plugins.host.GameLoader
import sc.plugins.PluginDescriptor
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment
import java.io.File

@PluginDescriptor(name = "Blokus", uuid = GamePlugin.PLUGIN_UUID)
class GamePlugin: IGamePlugin {
    companion object {
        const val PLUGIN_UUID = "swc_2021_blokus"
    }
    
    override fun id() = PLUGIN_UUID
    
    override fun createGame(): IGameInstance =
            Game()
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            Game(state as GameState)
    
    override val scoreDefinition: ScoreDefinition =
            ScoreDefinition(arrayOf(
                    ScoreFragment("Gewinner"),
                    ScoreFragment("\u2205 Punkte", ScoreAggregation.AVERAGE)
            ))
    
}
