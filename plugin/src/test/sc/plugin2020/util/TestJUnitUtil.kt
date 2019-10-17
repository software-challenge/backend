package sc.plugin2020.util

import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import org.junit.Assert.assertEquals

object TestJUnitUtil {
    
    @JvmStatic
    fun assertContentEquals(expected: Collection<*>, actual: Collection<*>) {
        assertEquals("Size of collections does not match", expected.size, actual.size)
        assertTrue("Unexpected Elements!\n" +
                "Expected: $expected\n" +
                "Actual: $actual", expected.containsAll(actual))
        assertTrue("Missing Elements!\n" +
                "Expected: $expected\n" +
                "Actual: $actual", actual.containsAll(expected))
    }
    
    @JvmStatic
    fun assertThrows(clazz: Class<out Exception>, run: () -> Unit) {
        try {
            run()
            fail()
        } catch(e: Exception) {
            assertTrue(e.toString(), clazz.isInstance(e))
        }
        
    }
    
    @JvmStatic
    fun assertThrows(clazz: Class<out Exception>, run: () -> Unit, message: String) {
        try {
            run()
            fail()
        } catch(e: Exception) {
            assertTrue(e.toString(), clazz.isInstance(e) && e.message == message)
        }
        
    }
    
}
