package sc.server.client

import io.kotest.assertions.until.fixed
import io.kotest.assertions.until.until
import io.kotest.matchers.shouldBe
import sc.api.plugins.host.IPlayerListener
import sc.protocol.responses.ProtocolMessage
import java.util.Queue
import java.util.ArrayDeque
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

class PlayerListener : IPlayerListener {
    private val messages: Queue<ProtocolMessage> = ArrayDeque()

    override fun onPlayerEvent(request: ProtocolMessage) {
        messages.add(request)
    }
    
    /** Clears all messages.
     * @return number of cleared messages */
    fun clearMessages(): Int {
        val size = messages.size
        messages.clear()
        return size
    }
    
    @ExperimentalTime
    suspend fun waitForMessage(messageType: KClass<out ProtocolMessage>, duration: Duration = 2.seconds) {
        until(duration, 100.milliseconds.fixed()) {
            messages.isNotEmpty()
        }
        messages.remove()::class shouldBe messageType
    }
}