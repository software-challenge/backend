package sc.api.plugins.exceptions

/** An exception within the flow of a Game. */
open class GameLogicException @JvmOverloads constructor(message: String, cause: Throwable? = null):
        GameException(message, cause)
