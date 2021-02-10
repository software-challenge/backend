package sc.api.plugins.exceptions


/** An Exception in Client-Server communication that should be handled. */
open class RescuableClientException
@JvmOverloads constructor(override val message: String, cause: Throwable? = null):
        Exception(message, cause)
