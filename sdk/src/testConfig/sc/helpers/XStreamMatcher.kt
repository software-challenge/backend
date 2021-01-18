package sc.helpers

import com.thoughtworks.xstream.XStream
import io.kotest.matchers.shouldBe
import sc.networking.XStreamProvider

val testXStream by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    XStreamProvider.loadPluginXStream()
}

infix fun <T : Any> T.shouldSerializeTo(serialized: String)
    = checkSerialization(testXStream, this, serialized)

fun <T: Any> checkSerialization(xStream: XStream, obj: T, serialized: String) {
    xStream.toXML(obj) shouldBe serialized
    val deserialized = xStream.fromXML(serialized)
    deserialized.javaClass shouldBe obj.javaClass
    deserialized shouldBe obj
}
