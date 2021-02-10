package sc.server.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.Player
import sc.protocol.responses.ProtocolMessage
import sc.server.helpers.TestTeam
import sc.shared.*

class TestGame : AbstractGame<Player>(TestPlugin.TEST_PLUGIN_UUID) {
    val state = TestGameState()
    
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage) {
        if (data is TestMove) {
            data.perform(state)
            next(if (state.currentPlayer === TestTeam.RED) state.red else state.blue)
        } else throw InvalidMoveException(TestMoveMistake.INVALID_FORMAT)
    }

    override fun checkWinCondition(): WinCondition? {
        return if (state.round > 1) {
            WinCondition(if (state.state % 2 == 0) TestTeam.RED else TestTeam.BLUE, TestWinReason.WIN)
        } else null
    }

    override fun onPlayerJoined(): Player {
        if (players.size < 2) {
            return if (players.isEmpty()) {
                state.red
            } else {
                state.blue
            }.also { players.add(it) }
        }
        throw TooManyPlayersException()
    }

    override val playerScores: List<PlayerScore> = emptyList()

    override val currentState: IGameState
        get() = state

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

    override val winners: List<Player> = emptyList()

    override fun getTimeoutFor(player: Player): ActionTimeout {
        return ActionTimeout(false, 100000000L, 20000000L)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestGame::class.java)
    }
}
