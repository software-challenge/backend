package sc.server.client

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.runBlocking
import sc.server.network.await
import java.util.ArrayDeque
import java.util.Queue
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.ExperimentalTime

open class MessageListener<T: Any> {
    private val messages: Queue<T> = ArrayDeque()
    
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
    @ExperimentalTime
    fun <U: T> waitForMessage(messageType: KClass<out U>): U = runBlocking {
        await("Expected to receive ${messageType.simpleName}") {
            messages.shouldNotBeEmpty()
        }
        val msg = messages.remove()
        msg should beInstanceOf(messageType)
        messageType.cast(msg)
    }
}