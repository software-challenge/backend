package sc.plugin2021

import sc.framework.plugins.Player
import sc.shared.GameResult
import sc.shared.Team

interface IGameHandler {
    
    /**
     * wird aufgerufen, wenn Spieler aktualisiert werden
     *
     * @param player      eigener Spieler
     * @param otherPlayer anderer Spieler
     */
    fun onUpdate(player: Player, otherPlayer: Player)
    
    /** wird aufgerufen, wenn sich das Spielbrett aendert. */
    fun onUpdate(gamestate: GameState)
    
    /** wird aufgerufen, wenn der Spieler zum Zug aufgefordert wurde. */
    fun onRequestAction()
    
    /**
     * sendet dem Spielserver den übergebenen Zug
     *
     * @param move zu taetigender Zug
     */
    fun sendAction(move: Move)
    
    /**
     * aufgerufen, wenn das Spiel beendet ist.
     *
     * @param data         mit getScores() kann man die Punkte erfragen
     * @param color        Playercolor
     * @param errorMessage Fehlernachricht
     */
    fun gameEnded(data: GameResult, color: Team, errorMessage: String)
    
}