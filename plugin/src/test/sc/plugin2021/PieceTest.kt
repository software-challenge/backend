package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class PieceTest: StringSpec({
    "Test Piece initialisation" {
        for (pieceShape in PieceShape.shapes) {
            Piece(Color.GREEN, pieceShape.key, Rotation.NONE, false).shape shouldBe pieceShape.value
        }
        
        Piece(Color.YELLOW, 4, Rotation.RIGHT, false).toString() shouldBe "YELLOW Piece 4:1"
        Piece(Color.RED, 20, Rotation.LEFT, false).toString() shouldBe "RED Piece 20:3"
        Piece(Color.BLUE, 15, Rotation.MIRROR, true).toString() shouldBe "BLUE Piece 15:2 (flipped)"
    }
    "Test PieceShape arithmetic" {
        PieceShape(setOf(Coordinates(1, 2), Coordinates(3, 2))).coordinates.shouldBe(
                setOf(Coordinates(0, 0), Coordinates(2, 0))
        )
        
        for (pair in PieceShape.shapes) {
            val shape = pair.value
            shape.rotate(Rotation.NONE) shouldBe shape
            shape.rotate(Rotation.RIGHT).rotate(Rotation.RIGHT) shouldBe shape.rotate(Rotation.MIRROR)
            shape.rotate(Rotation.MIRROR).rotate(Rotation.MIRROR) shouldBe shape
            shape.rotate(Rotation.LEFT) shouldBe shape.rotate(Rotation.MIRROR).rotate(Rotation.RIGHT)
            shape.flip(false) shouldBe shape
            shape.flip(true).flip() shouldBe shape
        }
    }
})