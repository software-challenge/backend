package sc.server.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.Player
import sc.framework.plugins.RoundBasedGameInstance
import sc.protocol.responses.ProtocolMessage
import sc.server.helpers.TestTeam
import sc.shared.*
import java.util.*

class TestGame : RoundBasedGameInstance<TestPlayer?>(TestPlugin.TEST_PLUGIN_UUID) {
    private val state = TestGameState()
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage) {
        /*
         * NOTE: Checking if right player sent move was already done by
         * {@link sc.framework.plugins.RoundBasedGameInstance#onAction(Player, Object)}.
         * There is no need to do it here again.
         */

        if (data is TestMove) {
            data.perform(state)
            next(if (state.currentPlayer === TestTeam.RED) state.red else state.blue)
        }
    }

    override fun checkWinCondition(): WinCondition? {
        return if (this.round > 1) {
            logger.info("Someone won")
            WinCondition(if (state.state % 2 == 0) TestTeam.RED else TestTeam.BLUE, TestWinReason.WIN)
        } else null
    }

    override fun onPlayerJoined(): Player {
        if (players.size < 2) {
            return if (players.isEmpty()) {
                state.red = TestPlayer(TestTeam.RED)
                players.add(state.red)
                state.red
            } else {
                state.blue = TestPlayer(TestTeam.BLUE)
                players.add(state.blue)
                state.blue
            }
        }
        throw TooManyPlayersException()
    }

    val testPlayers: List<TestPlayer?>
        get() = players

    override fun getPlayerScores(): List<PlayerScore> = emptyList()

    override fun getCurrentState(): IGameState = state

    override fun onPlayerLeft(player: Player, cause: ScoreCause?) {
        // this.players.remove(player);
        logger.debug("Player left $player")
        val result = generateScoreMap()
        result[player] = PlayerScore(cause, "Spieler hat das Spiel verlassen.", 0)
        notifyOnGameOver(result)
    }

    override fun onPlayerLeft(player: Player) {
        onPlayerLeft(player, ScoreCause.LEFT)
    }

    override fun getScoreFor(p: TestPlayer?): PlayerScore? {
        return PlayerScore(true, "Spieler hat gewonnen.")
    }

    override fun loadFromFile(file: String) {}
    override fun loadFromFile(file: String, turn: Int) {}
    override fun loadGameInfo(gameInfo: Any) {}
    override fun getWinners(): List<Player> = emptyList()

    override fun getPlayers(): List<Player> = ArrayList<Player>(players)

    /** Sends welcomeMessage to all listeners and notify player on new gameStates or MoveRequests  */
    override fun start() {
        for (p in players) {
            p!!.notifyListeners(WelcomeMessage(p.color))
        }
        super.start()
    }

    override fun getTimeoutFor(player: TestPlayer?): ActionTimeout? {
        return ActionTimeout(false, 100000000L, 20000000L)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestGame::class.java)
    }
}