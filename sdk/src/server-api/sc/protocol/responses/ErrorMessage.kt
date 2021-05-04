package sc.protocol.responses

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.protocol.RoomMessage

/** Response to an erroneous message, including an error [message]. */
@XStreamAlias("error")
data class ErrorMessage(
        val originalMessage: RoomMessage,
        @XStreamAsAttribute
        val message: String,
): RoomMessage {
    val logMessage
        get() = "$originalMessage caused an error: $message"
}
