package sc.player

import sc.api.plugins.IGameState
import sc.api.plugins.IMove
import sc.shared.GameResult

/**
 * Das Interface der Logik.
 * Der GameHandler definiert Reaktionen auf Ereignisse vom Server,
 * insbesondere die Reaktion mit einem Zug auf eine Zuganfrage.
 */
interface IGameHandler {
    
    /** Wird aufgerufen, wenn sich das Spielbrett ändert. */
    fun onUpdate(gameState: IGameState)
    
    /** Wird aufgerufen, um die Zuganfrage des Servers zu beantworten. */
    fun calculateMove(): IMove
    
    /** Wird aufgerufen, wenn das Spiel beendet ist. */
    fun onGameOver(data: GameResult)
    
    /** Wird aufgerufen, wenn der Server einen Fehler meldet.
     * Bedeutet auch den Abbruch des Spiels. */
    fun onError(error: String)
    
}
