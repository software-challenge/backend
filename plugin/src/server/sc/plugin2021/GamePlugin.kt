package sc.plugin2021

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.host.IGamePluginHost
import sc.plugin2021.util.Configuration.classesToRegister
import sc.plugins.PluginDescriptor
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment

@PluginDescriptor(name = "Blokus", uuid = "swc_2021_blokus", author = "")
class GamePlugin: IGamePlugin {
    
    companion object {
        val PLUGIN_AUTHOR = ""
        val PLUGIN_UUID = "swc_2021_blokus"
        
        val SCORE_DEFINITION = ScoreDefinition(arrayOf(
                ScoreFragment("Gewinner"),
                ScoreFragment("\u2205 Punkte", ScoreAggregation.AVERAGE)
        ))
    }
    
    override fun createGame(): IGameInstance {
        return Game()
    }
    
    override fun initialize(host: IGamePluginHost) {
        host.registerProtocolClasses(classesToRegister)
    }
    
    override fun unload() {
        TODO("Not yet implemented")
    }
    
    override fun getScoreDefinition(): ScoreDefinition = SCORE_DEFINITION
}
