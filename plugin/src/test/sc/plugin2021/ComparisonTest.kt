package sc.plugin2021

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.StringSpec
import sc.plugin2021.util.Constants

class ComparisonTest: StringSpec({
    "Coordinate comparison" {
        Coordinates(3, 2) shouldBe Coordinates(3, 2)
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