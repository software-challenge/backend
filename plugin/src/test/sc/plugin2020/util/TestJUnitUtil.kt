package sc.plugin2020.util

import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail

object TestJUnitUtil {

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
