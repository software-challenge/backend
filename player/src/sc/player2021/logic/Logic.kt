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
 * aber gülltige Züge macht.
 * Außerdem werden alle aktionen geloggt und in der Konsole ausgegeben.
 */
class Logic(private val client: SimpleClient): IGameHandler{
    companion object {
        val logger = LoggerFactory.getLogger(Logic::class.java)
    }
    
    private lateinit var gameState: GameState
    private lateinit var currentPlayer: Player
    
    override fun onUpdate(player: Player, otherPlayer: Player) {
        currentPlayer = player
        logger.info("Spielerwechsel - neuer Spieler: ${player.color}")
    }
    
    override fun onUpdate(gamestate: GameState) {
        this.gameState = gamestate
        currentPlayer = gamestate.currentPlayer
        logger.info("$gamestate")
    }
    
    override fun onRequestAction() {
        val startTime = System.currentTimeMillis()
        logger.info("Es wurde ein Zug angefordert.")
        val possibleMoves = GameRuleLogic.getPossibleMoves(gameState)
        sendAction(
                if (possibleMoves.isEmpty()) PassMove(gameState.currentColor)
                else possibleMoves.random())
    }
    
    override fun sendAction(move: Move) {
        client.sendMove(move)
    }
    
    override fun gameEnded(data: GameResult, team: Team?, errorMessage: String?) {
        logger.info("Das Spiel ist beendet.")
    }
    
    
}