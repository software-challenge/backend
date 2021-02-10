package sc.server.client

import io.kotest.assertions.timing.eventually
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
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
    fun waitForMessage(messageType: KClass<out ProtocolMessage>, duration: Duration = 200.milliseconds) = runBlocking {
        eventually(duration, 20.milliseconds.fibonacci()) {
            withClue("Expected ${messageType.simpleName} within $duration") {
                messages.shouldNotBeEmpty()
            }
        }
        messages.remove()::class shouldBe messageType
    }
}