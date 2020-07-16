package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IGameState
import sc.framework.plugins.RoundBasedGameInstance
import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage
import sc.shared.PlayerScore
import sc.shared.WinCondition


@XStreamAlias(value = "game")
class Game(UUID: String): RoundBasedGameInstance<Player>() {
    val availableTeams = mutableListOf(Team.ONE, Team.TWO)
    val gameState = GameState()
    
    init {
        pluginUUID = UUID
    }
    
    
    override fun getWinners(): MutableList<Player> {
        TODO("Not yet implemented")
    }
    
    override fun getPlayerScores(): MutableList<PlayerScore> {
        TODO("Not yet implemented")
    }
    
    override fun getPlayers(): MutableList<Player> {
        TODO("Not yet implemented")
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
    
    override fun onPlayerJoined(): Player {
        TODO("Not yet implemented")
    }
    
    override fun getScoreFor(p: Player?): PlayerScore {
        TODO("Not yet implemented")
    }
    
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage?) {
        TODO("Not yet implemented")
    }
    
    override fun getCurrentState(): IGameState {
        TODO("Not yet implemented")
    }
    
    
}