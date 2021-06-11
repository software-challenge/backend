package sc.api.plugins.exceptions

/** A [RescuableClientException] resulting from interacting with a GameRoom. */
open class GameRoomException
@JvmOverloads constructor(
        message: String,
        cause: Throwable? = null,
): RescuableClientException(message, cause)