package sc.plugin2021

import sc.framework.plugins.Player
import sc.shared.GameResult
import sc.api.plugins.ITeam

/**
 * Das Interface der Logik.
 * Der GameHandler definiert Reaktionen auf Ereignisse vom Server,
 * insbesondere die Reaktion mit einem Zug auf eine Zuganfrage.
 */
interface IGameHandler {
    
    /**
     * Wird aufgerufen, wenn Spieler aktualisiert werden.
     *
     * @param player      eigener Spieler
     * @param otherPlayer anderer Spieler
     */
    fun onUpdate(player: Player, otherPlayer: Player)
    
    /** Wird aufgerufen, wenn sich das Spielbrett ändert. */
    fun onUpdate(gamestate: GameState)
    
    /**
     * Wird aufgerufen, um die Zuganfrage des Servers zu beantworten
     */
    fun calculateMove(): Move
    
    /**
     * Wird aufgerufen, wenn das Spiel beendet ist.
     *
     * @param data         Das Spielergebnis
     * @param team         Team des Spielers
     * @param errorMessage Optionale Fehlernachricht
     */
    fun gameEnded(data: GameResult, team: Team?, errorMessage: String?)
    
}
