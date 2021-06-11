package sc.api.plugins.exceptions

/** An exception concerning the Game within a GameRoom. */
open class GameException
@JvmOverloads constructor(
        message: String,
        cause: Throwable? = null
): GameRoomException(message, cause)
