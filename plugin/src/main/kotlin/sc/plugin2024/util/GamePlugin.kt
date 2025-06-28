package sc.plugin2024.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.TwoPlayerGame
import sc.plugin2024.GameState
import sc.plugin2024.Move
import sc.shared.*

@XStreamAlias(value = "winreason")
enum class MQWinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    DIFFERING_SCORES("%s hat mehr Punkte."),
    DIFFERING_PASSENGERS("%S hat mehr Passagiere befördert."),
    SEGMENT_DISTANCE("%s liegt 3 Segmente vorne."),
    GOAL("%s hat das Ziel zuerst erreicht."),
    CRASHED("%s kann sich nicht mehr bewegen.", false);
}

class GamePlugin: IGamePlugin<Move> {
    companion object {
        const val PLUGIN_ID = "swc_2024_mississippi_queen"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen"), ScoreAggregation.SUM),
                        ScoreFragment("Punkte", MQWinReason.DIFFERING_SCORES, ScoreAggregation.AVERAGE),
                        ScoreFragment("Passagiere", MQWinReason.DIFFERING_PASSENGERS, ScoreAggregation.AVERAGE),
                ))
    }
    
    override val id = PLUGIN_ID
    
    override val name = "Mississippi Queen"
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val turnLimit: Int =
        MQConstants.ROUND_LIMIT * 2
    
    override val moveClass = Move::class.java
    
    override fun createGame(): IGameInstance =
            TwoPlayerGame(this, GameState())
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            TwoPlayerGame(this, state as GameState)
    
}
