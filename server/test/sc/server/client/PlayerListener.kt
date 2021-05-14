package sc.server.client

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import kotlinx.coroutines.runBlocking
import sc.api.plugins.host.IPlayerListener
import sc.protocol.room.RoomMessage
import sc.server.network.await
import java.util.ArrayDeque
import java.util.Queue
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime

class PlayerListener : IPlayerListener {
    private val messages: Queue<RoomMessage> = ArrayDeque()

    override fun onPlayerEvent(request: RoomMessage) {
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
    fun waitForMessage(messageType: KClass<out RoomMessage>) = runBlocking {
        await("Expected to receive ${messageType.simpleName}") {
            messages.shouldNotBeEmpty()
        }
        messages.remove() should beInstanceOf(messageType)
    }
}