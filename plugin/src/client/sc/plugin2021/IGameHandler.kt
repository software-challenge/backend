package sc.plugin2021

import sc.framework.plugins.Player
import sc.shared.GameResult
import sc.api.plugins.ITeam

/**
 * Das Interface der Logik.
 * Der GameHandler kommuniziert mit dem Server, d.h.
 * Er bekommt Events (Updates, Anfragen, Spielende) und
 * sendet beantwortet Zuganfragen mit einem entsprechenden Zug.
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
     * Wird aufgerufen, wenn der Spieler zum Zug aufgefordert wurde.
     *
     * @see sendAction
     */
    fun onRequestAction()
    
    /**
     * Sendet dem Spielserver den gegebenen Zug.
     * Diese Funktion sollte auf eine Zuganfrage vom Server folgen.
     *
     * @param move zu tätigender Zug
     */
    fun sendAction(move: Move)
    
    /**
     * Wird aufgerufen, wenn das Spiel beendet ist.
     *
     * @param data         Das Spielergebnis
     * @param team         Team des Spielers
     * @param errorMessage Optionale Fehlernachricht
     */
    fun gameEnded(data: GameResult, team: Team?, errorMessage: String?)
    
}