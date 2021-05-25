package sc.protocol.room

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Response to an erroneous message, including an error [message]. */
@XStreamAlias("error")
data class ErrorMessage(
        val originalMessage: RoomMessage,
        @XStreamAsAttribute
        val message: String,
): RoomOrchestrationMessage {
    val logMessage
        get() = "$originalMessage caused an error: $message"
}
