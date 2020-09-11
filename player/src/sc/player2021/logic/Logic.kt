package sc.player2021.logic

import org.slf4j.LoggerFactory
import sc.framework.plugins.Player
import sc.player2021.SimpleClient
import sc.plugin2021.*
import sc.plugin2021.util.GameRuleLogic
import sc.shared.GameResult

/**
 * Das Herz des Clients:
 * Eine sehr simple Logik, die ihre Züge zufällig wählt,
 * aber gültige Züge macht.
 * Außerdem werden alle aktionen geloggt und in der Konsole ausgegeben.
 */
class Logic(private val client: SimpleClient): IGameHandler{
    companion object {
        val logger = LoggerFactory.getLogger(Logic::class.java)
    }
    
    /** Der aktuelle Spielstand, aus dem der nächste Zug berechnet werden kann. */
    private lateinit var gameState: GameState
    /** Der Spieler, der momentan am Zug ist. */
    private lateinit var currentPlayer: Player
    
    /** Der aktuelle Spieler wird geupdatet. */
    override fun onUpdate(player: Player, otherPlayer: Player) {
        currentPlayer = player
        logger.info("Spielerwechsel - neuer Spieler: ${player.color}")
    }
    
    /** Der aktuelle Spielstand wird aktualisiert. */
    override fun onUpdate(gamestate: GameState) {
        this.gameState = gamestate
        currentPlayer = gamestate.currentPlayer
        logger.info("$gamestate")
    }
    
    /** Ein Zug wurde vom Server angefordert. */
    override fun onRequestAction() {
        val startTime = System.currentTimeMillis()
        logger.info("Es wurde ein Zug angefordert.")
        val possibleMoves = GameRuleLogic.getPossibleMoves(gameState)
        
        /** Ein zufälliger, valider Move wird an den Server zurückgesendet. */
        sendAction(
                if (possibleMoves.isEmpty()) PassMove(gameState.currentColor)
                else possibleMoves.random())
    }
    
    /** Der Zug wird an den Client geschickt, der diesen dann an den Server sendet. */
    override fun sendAction(move: Move) {
        client.sendMove(move)
    }
    
    /** Das Spiel ist beendet. */
    override fun gameEnded(data: GameResult, team: Team?, errorMessage: String?) {
        logger.info("Das Spiel ist beendet.")
    }
}