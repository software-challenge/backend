package sc.plugin2023

import sc.api.plugins.IMove
import sc.framework.plugins.AbstractGame
import sc.shared.MoveMistake
import sc.plugin2023.util.GamePlugin
import sc.shared.InvalidMoveException

class Game(override val currentState: GameState = GameState()): AbstractGame(GamePlugin()) {
    val isGameOver: Boolean
        get() = currentState.isOver
    
    override fun onRoundBasedAction(move: IMove) {
        if (move !is Move)
            throw InvalidMoveException(MoveMistake.INVALID_FORMAT)
        
        logger.debug("Performing {}", move)
        currentState.performMoveDirectly(move)
        logger.debug("Current State: {}", currentState.longString())
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
