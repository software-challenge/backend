package sc.plugin2027.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.TwoPlayerGame
import sc.plugin2027.GameState
import sc.plugin2027.Move
import sc.shared.*

/**
 * Eine Beschreibung, welches Team gewonnen hat und warum.
 */
@XStreamAlias(value = "winreason")
enum class BlokusWinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    /** Ein Team hat eine höhere Punktzahl als das andere. */
    DIFFERING_SCORES("%s hat am meisten Punkte erzielt.");
}

/**
 * Das Plugin, welches die Regeln und Informationen über das Spiel bereitstellt.
 */
class GamePlugin: IGamePlugin<Move> {
    companion object {
        const val PLUGIN_ID = "swc_2027_blokus"
        // FIXME what are the scores, it seems to be incorrect when playing.
        // Mabe add a test?
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen."), ScoreAggregation.SUM),
                        ScoreFragment("Punkte", BlokusWinReason.DIFFERING_SCORES, ScoreAggregation.AVERAGE),
                    ))
    }
    
    override val id = PLUGIN_ID
    
    override val name = "Blokus"
    
    override val scoreDefinition =
            Companion.scoreDefinition
    
    override val turnLimit: Int =
        Constants.ROUND_LIMIT * 2
    
    override val moveClass = Move::class.java
    
    override fun createGame(): IGameInstance =
            TwoPlayerGame(this, GameState())
    
    override fun createGameFromState(state: IGameState): IGameInstance =
            TwoPlayerGame(this, state as GameState)
    
}
