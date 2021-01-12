package sc.server.helpers

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
            if (isEqual(expected, action()))
                return true
            Thread.yield()
        }
        
        Assertions.assertTrue(isEqual(expected, action()), "Did not receive " + expected + " within " + millis + "ms")
        return isEqual(expected, action())
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
    
    fun isEqual(o1: Any?, o2: Any?): Boolean = o1 == o2
    
    fun waitMillis(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: Exception) {
        }
    }
    
    @JvmOverloads
    fun waitForObject(@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") o: Object, millis: Long = 0) {
        try {
            synchronized(o) {
                o.wait(millis)
            }
        } catch (e: Exception) {
        }
    }
    
}
