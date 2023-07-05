package sc.plugin2024

import org.slf4j.LoggerFactory
import sc.api.plugins.IMove
import sc.api.plugins.Team
import sc.framework.plugins.AbstractGame
import sc.shared.MoveMistake
import sc.plugin2024.util.WinReason
import sc.plugin2024.util.GamePlugin
import sc.shared.InvalidMoveException
import sc.shared.WinCondition

fun <T> Collection<T>.maxByNoEqual(selector: (T) -> Int): T? =
        fold(Int.MIN_VALUE to (null as T?)) { acc, pos ->
            val value = selector(pos)
            when {
                value > acc.first -> value to pos
                value == acc.first -> value to null
                else -> acc
            }
        }.second

class Game(override val currentState: GameState = GameState()): AbstractGame(GamePlugin.PLUGIN_ID) {
    companion object {
        val logger = LoggerFactory.getLogger(Game::class.java)
    }
    
    val isGameOver: Boolean
        get() = currentState.isOver
    
    override fun onRoundBasedAction(move: IMove) {
        if (move !is Move)
            throw InvalidMoveException(MoveMistake.INVALID_FORMAT)
        
        AbstractGame.logger.debug("Performing $move")
        currentState.performMove(move)
        AbstractGame.logger.debug("Current State: ${currentState.longString()}")
    }
    
    /**
     * Checks whether and why the game is over.
     *
     * @return null if any player can still move, otherwise a WinCondition with the winner and reason.
     */
    override fun checkWinCondition(): WinCondition? {
        if (!isGameOver) return null
        return Team.values().toList().maxByNoEqual { currentState.getPointsForTeam(it).first() }?.let {
            WinCondition(it, WinReason.DIFFERING_SCORES)
        } ?: WinCondition(null, WinReason.EQUAL_SCORE)
    }
    
    override fun toString(): String =
            "Game(${
                when {
                    isGameOver -> "OVER, "
                    isPaused -> "PAUSED, "
                    else -> ""
                }
            }players=$players, gameState=$currentState)"
    
}
