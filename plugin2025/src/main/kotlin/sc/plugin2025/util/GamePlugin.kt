package sc.plugin2025.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.TwoPlayerGame
import sc.plugin2025.GameState
import sc.plugin2025.Move
import sc.shared.*

@XStreamAlias(value = "winreason")
enum class HuIWinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    DIFFERING_SCORES("%s ist weiter vorne."),
    DIFFERING_CARROTS("%S hat mehr Karotten übrig."),
    GOAL("%s hat das Ziel zuerst erreicht."),
}

class GamePlugin: IGamePlugin<Move> {
    companion object {
        const val PLUGIN_ID = "swc_2025_hase_und_igel"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen"), ScoreAggregation.SUM),
                        ScoreFragment("Feld", HuIWinReason.DIFFERING_SCORES, ScoreAggregation.AVERAGE),
                        ScoreFragment("Karotten", HuIWinReason.DIFFERING_CARROTS, ScoreAggregation.AVERAGE),
                ))
    }
    
    override val id = PLUGIN_ID
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val turnLimit: Int =
        HuIConstants.ROUND_LIMIT * 2
    
    override val moveClass = Move::class.java
    
    override fun createGame(): IGameInstance =
            TwoPlayerGame(this, GameState())
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            TwoPlayerGame(this, state as GameState)
    
}
