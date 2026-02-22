package sc.server.client

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import kotlinx.coroutines.runBlocking
import sc.server.network.await
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.ExperimentalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class MessageListener<T: Any> {
    protected val messages: Queue<T> = ConcurrentLinkedQueue()
    
    fun addMessage(message: T) =
            messages.add(message)
    
    /** Clears all messages.
     * @return number of cleared messages */
    fun clearMessages(): Int {
        val size = messages.size
        messages.clear()
        return size
    }
    
    /** Waits until a message arrives and asserts its type.
     * @return the message. */
    fun <U: T> waitForMessage(messageType: KClass<out U>, timeout: Duration = 5.seconds): U = runBlocking {
        while(true) {
            await("Expected to receive ${messageType.simpleName}", duration = timeout) {
                messages.shouldNotBeEmpty()
            }
            val msg = messages.remove()
            if(messageType.isInstance(msg)) {
                msg should beInstanceOf(messageType)
                return@runBlocking messageType.cast(msg)
            }
        }
        error("Unreachable")
    }
}
