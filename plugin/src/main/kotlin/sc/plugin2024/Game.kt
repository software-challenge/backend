package sc.plugin2024

import sc.api.plugins.IMove
import sc.framework.plugins.AbstractGame
import sc.shared.MoveMistake
import sc.plugin2024.util.GamePlugin
import sc.shared.InvalidMoveException

fun <T> Collection<T>.maxByNoEqual(selector: (T) -> Int): T? =
        fold(Int.MIN_VALUE to (null as T?)) { acc, pos ->
            val value = selector(pos)
            when {
                value > acc.first -> value to pos
                value == acc.first -> value to null
                else -> acc
            }
        }.second

class Game(override val currentState: GameState = GameState()): AbstractGame(GamePlugin()) {
    
    override fun onRoundBasedAction(move: IMove) {
        if(move !is Move)
            throw InvalidMoveException(MoveMistake.INVALID_FORMAT)
        
        logger.debug("Performing {}", move)
        currentState.performMoveDirectly(move)
        logger.debug("Current State: ${currentState.longString()}")
    }
    
    override fun toString(): String =
            "Game(${
                when {
                    currentState.isOver -> "OVER, "
                    isPaused -> "PAUSED, "
                    else -> ""
                }
            }players=$players, gameState=$currentState)"
    
}