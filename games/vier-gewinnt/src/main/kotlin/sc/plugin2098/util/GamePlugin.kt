package sc.plugin2098.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.TwoPlayerGame
import sc.plugin2098.GameState
import sc.plugin2098.Move
import sc.shared.*

/** Gewinn-Gründe für 4 Gewinnt. */
@XStreamAlias(value = "winreason")
enum class Connect4WinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    CONNECTED_FOUR("%s hat 4 Plätchen in eine Reihe verbunden")
}

/** Plugin-Implementierung für das Vier-Gewinnt-Spiel. */
class GamePlugin: IGamePlugin<Move> {
    /** @suppress */
    companion object {
        const val PLUGIN_ID = "swc_2098_connect_4"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen."), ScoreAggregation.SUM),
                ))
    }
    
    /** Eindeutige Plugin-ID. */
    override val id = PLUGIN_ID
    
    /** Anzeigename des Spiels. */
    override val name = "4 Gewinnt!"
    
    /** Definition der Wertung und Auswertung. */
    override val scoreDefinition =
            Companion.scoreDefinition
    
    /** Maximale Zuganzahl (beide Spieler). */
    override val turnLimit: Int =
        Connect4Constants.ROUND_LIMIT * 2
    
    /** Klasse der Zug-Implementierung. */
    override val moveClass = Move::class.java
    
    /** Erstellt eine neue Spielinstanz im Startzustand. */
    override fun createGame(): IGameInstance =
            TwoPlayerGame(this, GameState())
    
    /** Erstellt eine Spielinstanz aus einem vorhandenen [state]. */
    override fun createGameFromState(state: IGameState): IGameInstance =
            TwoPlayerGame(this, state as GameState)
    
}
