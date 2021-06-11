package sc.api.plugins

import sc.api.plugins.exceptions.PluginLoaderException
import sc.shared.ScoreDefinition
import java.util.ServiceLoader

interface IGamePlugin {
    val id: String
    val scoreDefinition: ScoreDefinition
    val gameTimeout: Int
    
    /** @return eine neues Spiel. */
    fun createGame(): IGameInstance
    /** @return eine neues Spiel mit dem gegebenen GameState. */
    fun createGameFromState(state: IGameState): IGameInstance
    
    companion object {
        @JvmStatic
        fun loadPlugins(): Iterator<IGamePlugin> =
            ServiceLoader.load(IGamePlugin::class.java).iterator().takeIf {
                it.hasNext()
            } ?: throw PluginLoaderException("Could not find any game plugin")
        
        @JvmStatic
        fun loadPlugin(gameType: String): IGamePlugin = loadPlugins().asSequence().find {
            it.id == gameType
        } ?: throw PluginLoaderException("Could not find game of type '$gameType'")
        
        @JvmStatic
        fun loadPlugin(): IGamePlugin = loadPlugins().next()
        
        @JvmStatic
        fun loadPluginId(): String =
                loadPlugin().id
    }
}