package sc.plugin2021

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.host.IGamePluginHost
import sc.plugin2021.util.Configuration.classesToRegister
import sc.plugins.PluginDescriptor
import sc.shared.ScoreDefinition

@PluginDescriptor(name = "Blokus", uuid = "swc_2021_blokus", author = "")
class GamePlugin: IGamePlugin {
    
    companion object {
        val PLUGIN_AUTHOR = ""
        val PLUGIN_UUID = "swc_2021_blokus"
    }
    
    override fun createGame(): IGameInstance {
        return Game(PLUGIN_UUID)
    }
    
    override fun initialize(host: IGamePluginHost) {
        host.registerProtocolClasses(classesToRegister)
    }
    
    override fun unload() {
        TODO("Not yet implemented")
    }
    
    override fun getScoreDefinition(): ScoreDefinition {
        TODO("Not yet implemented")
    }
    
}
