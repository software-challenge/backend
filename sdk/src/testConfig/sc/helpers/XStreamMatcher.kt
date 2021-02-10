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

inline infix fun <reified T : Any> T.shouldSerializeTo(serialized: String)
    = checkSerialization(testXStream, this, serialized)

inline fun <reified T: Any> checkSerialization(
        xStream: XStream, obj: T, serialized: String,
        matcher: (obj: T, deserialized: T) -> Unit = { original, deserialized -> deserialized shouldBe original }) {
    xStream.toXML(obj) shouldBe serialized
    val deserialized = xStream.fromXML(serialized)
    deserialized.javaClass shouldBe obj.javaClass
    matcher(obj, deserialized as T)
}
