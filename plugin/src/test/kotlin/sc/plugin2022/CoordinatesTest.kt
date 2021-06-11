package sc.plugin2022

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class CoordinatesTest: WordSpec({
    "Coordinates" should {
        val position = Coordinates(3, 2)
        "be equal to itself" {
            position shouldBe position
        }
        "be equal to the same position" {
            position shouldBe Coordinates(3, 2)
            position shouldNotBeSameInstanceAs Coordinates(3, 2)
        }
        "not be equal to other positions" {
            position shouldNotBe Coordinates(2, 3)
            position shouldNotBe Coordinates(-3, 2)
            position shouldNotBe Coordinates(-3, -2)
            position shouldNotBe Coordinates(3, -2)
        }
        "not be equal to instances other classes" {
            position shouldNotBe Board()
        }
    }
})