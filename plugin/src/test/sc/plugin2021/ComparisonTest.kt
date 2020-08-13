package sc.plugin2021

import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import sc.plugin2021.util.Constants

class ComparisonTest: StringSpec({
    "Coordinate comparison" {
        Coordinates(3, 2) shouldBe Coordinates(3, 2)
        Coordinates(3, 2) shouldNotBeSameInstanceAs  Coordinates(3, 2)
        Coordinates(3, 2) shouldNotBe Coordinates(2, 3)
        Coordinates(3, 2) shouldNotBe Board()
    }
    "Piece comparison" {
        Rotation.values().map {
            SetMove(Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = true, position = Coordinates(7, 12)))
            SetMove(Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = false, position = Coordinates(7, 12)))
        }.toSet().size shouldBe 1
    }
    "SetMove comparison" {
        Rotation.values().map {
            SetMove(Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = true, position = Coordinates(7, 12)))
            SetMove(Piece(kind = PieceShape.TETRO_O, rotation = it, isFlipped = false, position = Coordinates(7, 12)))
        }.toSet().size shouldBe 1
    }
})