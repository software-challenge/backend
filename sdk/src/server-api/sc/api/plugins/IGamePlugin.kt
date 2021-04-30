package sc.api.plugins

import sc.plugins.IPlugin
import sc.shared.ScoreDefinition
import java.io.File

interface IGamePlugin: IPlugin {
    fun id(): String?
    /** @return eine neues Spiel dieses Typs. */
    fun createGame(): IGameInstance
    fun createGameFromState(state: IGameState): IGameInstance
    
    val scoreDefinition: ScoreDefinition
}