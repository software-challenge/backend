package sc.api.plugins

import sc.api.plugins.exceptions.PluginLoaderException
import sc.framework.plugins.Constants
import sc.shared.ScoreDefinition
import java.util.ServiceLoader

interface IGamePlugin<M : IMove> {
    /** Plugin identifier for the protocol. */
    val id: String
    /** Arrangement of ScoreFragments in the GameResult. */
    val scoreDefinition: ScoreDefinition
    /**
     * Maximum Turns allowed in a game.
     * Used to detect unresponsive games.
     */
    val turnLimit: Int
    
    val gameTimeout
        get() = turnLimit * Constants.SOFT_TIMEOUT
    
    val moveClass: Class<M>
    
    /** @return ein neues Spiel. */
    fun createGame(): IGameInstance
    /** @return ein neues Spiel mit dem gegebenen GameState. */
    fun createGameFromState(state: IGameState): IGameInstance
    
    companion object {
        @JvmStatic
        fun loadPlugins(): Iterator<IGamePlugin<*>> =
                ServiceLoader.load(IGamePlugin::class.java).iterator().takeIf {
                    it.hasNext()
                } ?: throw PluginLoaderException("Could not find any game plugin")
        
        /** @param gameType id of the plugin, if null return any
         * @return The plugin with an id equal to [gameType]. */
        @JvmStatic
        fun loadPlugin(gameType: String?): IGamePlugin<*> =
                loadPlugins().asSequence().find {
                    gameType == null || it.id == gameType
                } ?: throw PluginLoaderException("Could not find game of type '$gameType'")
        
        @JvmStatic
        fun loadPlugin(): IGamePlugin<*> =
                loadPlugins().next()
        
        @JvmStatic
        fun loadPluginId(): String =
                loadPlugin().id
    }
}