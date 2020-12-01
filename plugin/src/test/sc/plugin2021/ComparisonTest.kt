package sc.plugin2021

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class ComparisonTest: WordSpec({
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
    "Piece comparison" should {
        "consider coordinates only" {
            Rotation.values().map {
                Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = true, position = Coordinates(7, 12))
                Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = false, position = Coordinates(7, 12))
            }.toSet() shouldHaveSize 1
        }
    }
    "SetMove comparison" should {
        "consider their internal piece only" {
            Rotation.values().map {
                SetMove(Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = true, position = Coordinates(7, 12)))
                SetMove(Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = false, position = Coordinates(7, 12)))
            }.toSet() shouldHaveSize 1
        }
    }
})