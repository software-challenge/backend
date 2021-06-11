package sc.networking.clients

import sc.shared.GameResult

interface IControllableGame {
    fun next()
    fun hasNext(): Boolean
    
    fun previous()
    fun pause()
    fun unpause()
    fun hasPrevious(): Boolean
    val isPaused: Boolean
    fun cancel()
    val currentState: Any?
    val currentError: Any?
    val isAtEnd: Boolean
    val isAtStart: Boolean
    fun goToFirst()
    fun goToLast()
    fun canTogglePause(): Boolean
    val result: GameResult?
    val isGameOver: Boolean
    val isReplay: Boolean
}