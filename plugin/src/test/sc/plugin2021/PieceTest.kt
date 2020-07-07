package sc.plugin2021

import arrow.core.Right
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class PieceTest: StringSpec({
    "Test Piece initialisation" {
        for (pieceShape in pieceShapes) {
            Piece(pieceShape.first, Rotation.NONE, PlayerColor.GREEN).shape shouldBe pieceShape.second
        }
        
        Piece(4, Rotation.RIGHT, PlayerColor.YELLOW).toString() shouldBe "YELLOW Piece 4:1"
        Piece(20, Rotation.LEFT, PlayerColor.RED).toString() shouldBe "RED Piece 20:3"
    }
    "Test PieceShape arithmetic" {
        PieceShape(setOf(Coordinates(1, 2), Coordinates(3, 2))).coordinates.shouldBe(
                setOf(Coordinates(0, 0), Coordinates(2, 0))
        )
        
        for (pair in pieceShapes) {
            val pieceShape = pair.second
            pieceShape.rotate(Rotation.NONE) shouldBe pieceShape
            pieceShape.rotate(Rotation.RIGHT).rotate(Rotation.RIGHT) shouldBe pieceShape.rotate(Rotation.MIRROR)
            pieceShape.rotate(Rotation.MIRROR).rotate(Rotation.MIRROR) shouldBe pieceShape
            pieceShape.rotate(Rotation.LEFT) shouldBe pieceShape.rotate(Rotation.MIRROR).rotate(Rotation.RIGHT)
        }
    }
})