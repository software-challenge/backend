package sc.helpers

import com.thoughtworks.xstream.XStream
import io.kotest.matchers.shouldBe

infix fun <T : Any> T.shouldSerializeTo(serialized: String)
    = checkSerialization(xStream, this, serialized)

fun <T: Any> checkSerialization(xStream: XStream, obj: T, serialized: String) {
    xStream.toXML(obj) shouldBe serialized
    val deserialized = xStream.fromXML(serialized)
    deserialized.javaClass shouldBe obj.javaClass
    deserialized shouldBe obj
}
