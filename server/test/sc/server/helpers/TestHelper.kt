package sc.server.helpers

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions
import java.util.concurrent.TimeUnit

object TestHelper {
    private const val DEFAULT_DURATION: Long = 100
    private val DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS
    
    @JvmOverloads
    fun <T> waitUntilEqual(expected: T, action: () -> T?, maxDuration: Long = DEFAULT_DURATION, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
        val millis = unit.toMillis(maxDuration)
        val timeout = System.currentTimeMillis() + millis
        
        while (System.currentTimeMillis() <= timeout) {
            if (expected == action())
                return true
            Thread.yield()
        }
        
        val value = action()
        withClue("Expected " + expected + " within " + millis + "ms") {
            value shouldBe expected
        }
        return expected == value
    }
    
    @JvmOverloads
    fun waitUntilTrue(action: () -> Boolean?, maxDuration: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean =
            waitUntilEqual(true, action, maxDuration, unit)
    
    @JvmOverloads
    fun waitUntilFalse(action: () -> Boolean?, maxDuration: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean =
            waitUntilEqual(false, action, maxDuration, unit)
    
    @JvmOverloads
    fun <T> assertEqualsWithTimeout(expected: T, action: () -> T?, maxDuration: Long = DEFAULT_DURATION, unit: TimeUnit = DEFAULT_TIME_UNIT) {
        waitUntilEqual(expected, action, maxDuration, unit)
        Assertions.assertEquals(expected, action())
    }
    
}
