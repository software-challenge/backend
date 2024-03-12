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
): AbstractGame(TestPlugin()) {
    
    override fun onRoundBasedAction(move: IMove) {
        if(move !is TestMove)
            throw InvalidMoveException(object: IMoveMistake {
                override val message = "TestGame only processes TestMove"
            })
        move.perform(currentState)
    }
    
    override fun onPlayerJoined(): Player =
            when(players.size) {
                0 -> currentState.red
                1 -> currentState.blue
                else -> throw TooManyPlayersException()
            }.also { players.add(it) }
    
    override fun getTimeoutFor(player: Player): ActionTimeout =
            ActionTimeout(false)
    
    override fun toString(): String =
            "TestGame(currentState=$currentState, paused=$isPaused, players=${players.joinToString { it.longString() }})"
}
