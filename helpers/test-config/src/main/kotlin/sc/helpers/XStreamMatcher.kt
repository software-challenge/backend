package sc.helpers

import com.thoughtworks.xstream.XStream
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.matchers.*
import sc.networking.XStreamProvider

@AutoScan
object XStreamProjectListener: ProjectListener {
    override suspend fun beforeProject() {
        testXStream = XStreamProvider.allPlugins()
    }
}

/** An XStream instance initialized with all active plugins. */
lateinit var testXStream: XStream

/** Asserts that the object is serialized to the given String
 * and is equal to the original after deserialization. */
inline infix fun <reified T: Any> T.shouldSerializeTo(serialized: String) =
        checkSerialization(testXStream, this, serialized.trimIndent())

/** Asserts that the object is serialized to the given String.
 * @param matcher checks the deserialized object, by default via equals and hashCode */
inline fun <reified T: Any> checkSerialization(
        xStream: XStream, obj: T, serialized: String,
        matcher: (obj: T, deserialized: T) -> Unit = { original, deserialized ->
            deserialized shouldBe original
            deserialized.hashCode() shouldBe original.hashCode()
        },
) {
    xStream.toXML(obj) shouldBe serialized
    val deserialized = xStream.fromXML(serialized)
    deserialized.javaClass shouldBe obj.javaClass
    matcher(obj, deserialized as T)
}
