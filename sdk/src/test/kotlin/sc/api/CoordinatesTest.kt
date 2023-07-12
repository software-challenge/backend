package sc.api

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import sc.api.plugins.Coordinates
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.HexDirection

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
    }
    "CubeCoordinates" should {
        val position = CubeCoordinates(3, 2)
        "be equal to itself" {
            forAll(position, CubeCoordinates(3, 2), position.rotatedBy(0), position.rotatedBy(-12)) {
                position shouldBe it
            }
            position shouldNotBeSameInstanceAs CubeCoordinates(3, 2)
        }
        "not be equal to other positions" {
            position shouldNotBe CubeCoordinates(2, 3)
            position shouldNotBe CubeCoordinates(-3, 2)
            position shouldNotBe CubeCoordinates(-3, -2)
            position shouldNotBe CubeCoordinates(3, -2)
        }
        "produce correct rotation" {
            CubeCoordinates(1, 0).rotatedBy(2) shouldBe CubeCoordinates(-1, 1)
            position.rotatedBy(1) shouldBe CubeCoordinates(-2, 5)
            position.rotatedBy(2) shouldBe position.rotatedBy(-4)
            position.rotatedBy(3) shouldBe position.rotatedBy(-3)
            position.rotatedBy(3) shouldBe position.unaryMinus()
        }
    }
    "HexDirections" should {
        "produce correct opposites" {
            forAll(
                    HexDirection.LEFT to HexDirection.RIGHT,
                    HexDirection.UP_LEFT to HexDirection.DOWN_RIGHT,
                    HexDirection.DOWN_LEFT to HexDirection.UP_RIGHT,
            ) {
                it.first.opposite() shouldBe it.second
                it.second.opposite() shouldBe it.first
            }
        }
    }
})