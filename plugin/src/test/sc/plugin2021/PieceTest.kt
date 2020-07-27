package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.plugin2021.util.Configuration


class PieceTest: StringSpec({
    "Test Piece initialisation" {
        for (pieceShape in PieceShape.shapes) {
            Piece(Color.GREEN, pieceShape.key, Rotation.NONE, false).shape shouldBe pieceShape.value
        }
        
        Piece(Color.YELLOW, 4, Rotation.RIGHT, false).toString() shouldBe "YELLOW Piece 4:1 [0,0]"
        Piece(Color.RED, 20, Rotation.LEFT, false).toString() shouldBe "RED Piece 20:3 [0,0]"
        Piece(Color.BLUE, 15, Rotation.MIRROR, true).toString() shouldBe "BLUE Piece 15:2 (flipped) [0,0]"
        Piece(Color.GREEN, 2, Rotation.NONE, true, Coordinates(5, 9)).toString() shouldBe "GREEN Piece 2:0 (flipped) [5,9]"
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
    "Piece coordination calculation" {
        val position = Coordinates(2, 2)
        val coordinates = setOf(Coordinates(2, 2), Coordinates(3, 2), Coordinates(2, 3))
        val piece = Piece(Color.RED, 2, Rotation.NONE, true, position)
    
        piece.coordinates shouldBe coordinates
    }
    "XML conversion" {
        val pieces: Map<Piece, String> = (listOf(
                Piece(Color.YELLOW, 4, Rotation.RIGHT, false),
                Piece(Color.RED, 20, Rotation.LEFT, false),
                Piece(Color.BLUE, 15, Rotation.MIRROR, true),
                Piece(Color.GREEN, 2, Rotation.NONE, true, Coordinates(5, 9))
        ) zip listOf(
                "YELLOW Piece 4:1 [0,0]",
                "RED Piece 20:3 [0,0]",
                "BLUE Piece 15:2 (flipped) [0,0]",
                "GREEN Piece 2:0 (flipped) [5,9]"
        )).toMap()
        
        pieces.forEach { println(Configuration.xStream.toXML(it.key)) }
    }
})