package sc.plugin2021

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.WordSpec

class RotationTest: WordSpec({
    "Rotations" should {
        "rotate properly" {
            Rotation.values().forEach {
                it.rotate(Rotation.NONE) shouldBe it
                Rotation.NONE.rotate(it) shouldBe it
                it.rotate(Rotation.RIGHT).rotate(Rotation.LEFT) shouldBe it
                it.rotate(Rotation.MIRROR).rotate(Rotation.MIRROR) shouldBe it
                it.rotate(Rotation.LEFT).rotate(Rotation.LEFT) shouldBe it.rotate(Rotation.MIRROR)
            }
        }
    }
})