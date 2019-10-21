package sc.protocol.helpers

import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage

data class RequestResult<T: ProtocolMessage>
@JvmOverloads constructor(
        var result: T? = null,
        var error: ProtocolErrorMessage? = null
) {
    
    /** @return true if [error] is null. */
    val isSuccessful: Boolean
        get() = this.error == null
    
    /**
     * Checks whether this object was set-up correctly.
     *
     * @return true if either result or error is set.
     */
    fun hasValidContents() = (this.result == null) xor (this.error == null)
    
}
