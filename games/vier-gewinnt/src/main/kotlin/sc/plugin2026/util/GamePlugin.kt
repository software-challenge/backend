package sc.plugin2026.util

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.framework.plugins.TwoPlayerGame
import sc.plugin2026.GameState
import sc.plugin2026.Move
import sc.shared.*

/** Gewinn-Gründe für Piranhas. */
@XStreamAlias(value = "winreason")
enum class PiranhasWinReason(override val message: String, override val isRegular: Boolean = true): IWinReason {
    /** Groesster zusammenhaengender Schwarm entscheidet. */
    BIGGER_SWARM("%s hat den größeren zusammenhängenden Schwarm"),
    /** Alle Fische einer Farbe zuerst vereinigt. */
    FIRST_UNION("%s hat zuerst alle Fische einer Farbe vereinigt"),
}

/** Plugin-Implementierung für das Piranhas-Spiel. */
class GamePlugin: IGamePlugin<Move> {
    /** @suppress */
    companion object {
        const val PLUGIN_ID = "swc_2026_piranhas"
        val scoreDefinition: ScoreDefinition =
                ScoreDefinition(arrayOf(
                        ScoreFragment("Siegpunkte", WinReason("%s hat gewonnen."), ScoreAggregation.SUM),
                        ScoreFragment("Schwarmgröße", PiranhasWinReason.BIGGER_SWARM, ScoreAggregation.AVERAGE),
                ))
    }
    
    /** Eindeutige Plugin-ID. */
    override val id = PLUGIN_ID
    
    /** Anzeigename des Spiels. */
    override val name = "Piranhas"
    
    /** Definition der Wertung und Auswertung. */
    override val scoreDefinition =
            Companion.scoreDefinition
    
    /** Maximale Zuganzahl (beide Spieler). */
    override val turnLimit: Int =
        PiranhaConstants.ROUND_LIMIT * 2
    
    /** Klasse der Zug-Implementierung. */
    override val moveClass = Move::class.java
    
    /** Erstellt eine neue Spielinstanz im Startzustand. */
    override fun createGame(): IGameInstance =
            TwoPlayerGame(this, GameState())
    
    /** Erstellt eine Spielinstanz aus einem vorhandenen [state]. */
    override fun createGameFromState(state: IGameState): IGameInstance =
            TwoPlayerGame(this, state as GameState)
    
}
