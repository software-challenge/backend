package sc.helpers

import com.thoughtworks.xstream.XStream
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.matchers.shouldBe
import sc.networking.XStreamProvider

@AutoScan
object XStreamProjectListener: ProjectListener {
    override suspend fun beforeProject() {
        testXStream = XStreamProvider.loadPluginXStream()
    }
}

lateinit var testXStream: XStream

infix fun <T : Any> T.shouldSerializeTo(serialized: String)
    = checkSerialization(testXStream, this, serialized)

fun <T: Any> checkSerialization(xStream: XStream, obj: T, serialized: String) {
    xStream.toXML(obj) shouldBe serialized
    val deserialized = xStream.fromXML(serialized)
    deserialized.javaClass shouldBe obj.javaClass
    deserialized shouldBe obj
}
