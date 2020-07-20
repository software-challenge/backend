package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.framework.plugins.RoundBasedGameInstance
import sc.framework.plugins.Player
import sc.plugin2021.util.GameRuleLogic
import sc.protocol.responses.ProtocolMessage
import sc.shared.*


@XStreamAlias(value = "game")
class Game(UUID: String = GamePlugin.PLUGIN_UUID): RoundBasedGameInstance<Player>() {
    companion object {
        val logger = LoggerFactory.getLogger(Game::class.java)
    }
    
    val availableTeams = mutableListOf(Team.ONE, Team.TWO)
    val gameState = GameState()
    
    init {
        pluginUUID = UUID
    }
    
    override fun start() {
        players.forEach {it.notifyListeners(WelcomeMessage(it.color)) }
        super.start()
    }
   
    override fun onPlayerJoined(): Player {
        val player = gameState.getPlayer(availableTeams.removeAt(0))
                ?: throw NullPointerException("Too many players joined the game!")
        
        players.add(player)
        gameState.addPlayer(player)
        return player
    }
    
    override fun getWinners(): MutableList<Player> {
        TODO("Not yet implemented")
    }
    
    override fun getPlayerScores(): MutableList<PlayerScore> {
        TODO("Not yet implemented")
    }
    
    override fun getPlayers(): MutableList<Player> {
        return players
    }
    
    override fun checkWinCondition(): WinCondition {
        TODO("Not yet implemented")
    }
    
    override fun loadGameInfo(gameInfo: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun loadFromFile(file: String?) {
        TODO("Not yet implemented")
    }
    
    override fun loadFromFile(file: String?, turn: Int) {
        TODO("Not yet implemented")
    }
    
    override fun getScoreFor(p: Player?): PlayerScore {
        TODO("Not yet implemented")
    }
    
    @Throws(InvalidMoveException::class, InvalidGameStateException::class)
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage?) {
        // This check is already done by super.onAction()
        assert(fromPlayer == activePlayer)
        
        try {
            if (data !is Move)
                throw InvalidMoveException("${fromPlayer.displayName} hat keinen validen Zug gesendet.")
            logger.debug("Performing Move $data")
            logger.debug("Current Board: ${gameState.board}")
            GameRuleLogic.performMove(gameState, data)
            next(gameState.currentPlayer)
        } catch(e: InvalidMoveException) {
            super.catchInvalidMove(e, fromPlayer)
        }
    }
    
    override fun getCurrentState(): IGameState {
        TODO("Not yet implemented")
    }
    
    
}