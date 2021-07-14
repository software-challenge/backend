package sc.plugin2022

import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.plugin2022.util.Constants
import sc.shared.ScoreAggregation
import sc.shared.ScoreDefinition
import sc.shared.ScoreFragment

class GamePlugin: IGamePlugin {
    companion object {
        const val PLUGIN_ID = "swc_2022_ostseeschach"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte"),
                        ScoreFragment("Bernsteine", ScoreAggregation.AVERAGE),
                        ScoreFragment("Figur vorne", ScoreAggregation.AVERAGE)
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
