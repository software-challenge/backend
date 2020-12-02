package sc.server.helpers

import org.junit.Assert
import org.junit.Test
import sc.framework.plugins.IPerspectiveAware
import sc.framework.plugins.IPerspectiveProvider
import sc.helpers.xStream

class ConverterTest {
    class HasSecrets(private val perspective: Any?) : IPerspectiveProvider, IPerspectiveAware {
        val secret = "i-am-secret"
        val unimportant = "i-am-unimportant"
        override fun getPerspective(): Any? {
            return perspective
        }

        override fun isVisibleFor(viewer: Any, field: String): Boolean {
            return !(field == "secret" && hacker == viewer)
        }

        companion object {
            val hacker = Any()
            val goodFriend = Any()
        }
    }

    @Test
    fun shouldSerializeSensitiveDataForAuthorizedPeople() {
        val data = HasSecrets(HasSecrets.goodFriend)
        val msg = xStream.toXML(data)
        Assert.assertNotSame(-1, msg.indexOf(data.secret))
        Assert.assertNotSame(-1, msg.indexOf(data.unimportant))
    }

    @Test
    fun shouldSerializeSensitiveDataForObservers() {
        val data = HasSecrets(null)
        val msg = xStream.toXML(data)
        Assert.assertNotSame(-1, msg.indexOf(data.secret))
        Assert.assertNotSame(-1, msg.indexOf(data.unimportant))
    }
}