package sc.server.plugins

import sc.api.plugins.IMove
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.Player
import sc.shared.*

data class TestGame(
        override val currentState: TestGameState = TestGameState(),
): AbstractGame(TestPlugin.TEST_PLUGIN_UUID) {
    
    override val playerScores: List<PlayerScore>
        get() = players.map { getScoreFor(it) }
    
    override fun onRoundBasedAction(move: IMove) {
        if (move !is TestMove)
            throw InvalidMoveException(object: IMoveMistake {
                override val message = "TestGame only processes TestMove"
            })
        move.perform(currentState)
    }
    
    override fun checkWinCondition(): WinCondition? {
        return if (currentState.round > 1) {
            WinCondition(if (currentState.state % 2 == 0) Team.ONE else Team.TWO, TestWinReason.WIN)
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
    
    override fun getScoreFor(player: Player) =
            when {
                player.hasLeft() ->
                    PlayerScore(ScoreCause.LEFT, "Spieler ist rausgeflogen.", 0)
                player.hasViolated() ->
                    PlayerScore(ScoreCause.RULE_VIOLATION, player.violationReason!!, 0)
                else ->
                    PlayerScore(true, "Spieler hat gewonnen.")
            }
    
    override fun getTimeoutFor(player: Player): ActionTimeout =
            ActionTimeout(false)
    
    override fun toString(): String =
            "TestGame(currentState=$currentState, paused=$isPaused, players=$players)"
}
