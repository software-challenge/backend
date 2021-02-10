package sc.server.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage
import sc.server.helpers.TestTeam
import sc.shared.*

data class TestGame(
        override val currentState: TestGameState = TestGameState(),
): AbstractGame<Player>(TestPlugin.TEST_PLUGIN_UUID) {
    
    override val playerScores: List<PlayerScore> = emptyList()
    override val winners: List<Player> = emptyList()
    
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage) {
        if (data is TestMove) {
            data.perform(currentState)
            next(if (currentState.currentPlayer === TestTeam.RED) currentState.red else currentState.blue)
        } else throw InvalidMoveException(TestMoveMistake.INVALID_FORMAT)
    }

    override fun checkWinCondition(): WinCondition? {
        return if (currentState.round > 1) {
            WinCondition(if (currentState.state % 2 == 0) TestTeam.RED else TestTeam.BLUE, TestWinReason.WIN)
        } else null
    }

    override fun onPlayerJoined(): Player {
        if (players.size < 2) {
            return if (players.isEmpty()) {
                currentState.red
            } else {
                currentState.blue
            }.also { players.add(it) }
        }
        throw TooManyPlayersException()
    }

    override fun onPlayerLeft(player: Player, cause: ScoreCause?) {
        // this.players.remove(player);
        logger.debug("Player left $player")
        val result = generateScoreMap().toMutableMap()
        result[player] = PlayerScore(cause ?: ScoreCause.LEFT, "Spieler hat das Spiel verlassen.", 0)
        notifyOnGameOver(result)
    }

    override fun getScoreFor(player: Player): PlayerScore {
        return PlayerScore(true, "Spieler hat gewonnen.")
    }

    override fun loadFromFile(file: String) {}
    override fun loadFromFile(file: String, turn: Int) {}
    override fun loadGameInfo(gameInfo: Any) {}
    
    override fun getTimeoutFor(player: Player): ActionTimeout =
            ActionTimeout(false, 100000000L, 20000000L)

    companion object {
        private val logger = LoggerFactory.getLogger(TestGame::class.java)
    }
}
