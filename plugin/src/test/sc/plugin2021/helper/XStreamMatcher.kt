package sc.plugin2021.helper

import io.kotest.matchers.shouldBe
import sc.plugin2021.GamePlugin

infix fun <T : Any> T.shouldSerializeTo(serialized: String) {
    GamePlugin.loadXStream().toXML(this) shouldBe serialized
    val deserialized = GamePlugin.loadXStream().fromXML(serialized)
    deserialized.toString() shouldBe this.toString()
    deserialized shouldBe this
}
