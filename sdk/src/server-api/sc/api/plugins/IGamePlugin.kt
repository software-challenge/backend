package sc.api.plugins

import sc.plugins.IPlugin
import sc.shared.ScoreDefinition
import java.util.ServiceLoader

interface IGamePlugin: IPlugin {
    fun id(): String
    /** @return eine neues Spiel dieses Typs. */
    fun createGame(): IGameInstance
    fun createGameFromState(state: IGameState): IGameInstance
    
    val scoreDefinition: ScoreDefinition
    
    companion object {
        @JvmStatic
        fun loadPlugin(): IGamePlugin {
            val iter = ServiceLoader.load(IGamePlugin::class.java).iterator()
            if (!iter.hasNext())
                throw RuntimeException("Couldn't find a plugin!")
            return iter.next()
        }
        
        @JvmStatic
        fun loadPluginId(): String =
                loadPlugin().id()
    }
}