package sc.plugin2021

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.helpers.shouldSerializeTo
import sc.plugin2021.util.align
import sc.plugin2021.util.flip
import sc.plugin2021.util.rotate

class PieceTest: WordSpec({
    "Pieces" When {
        "initialised without transformation" should {
            "retain the given shape" {
                for (pieceShape in PieceShape.shapes) {
                    Piece(Color.GREEN, pieceShape.key, Rotation.NONE, false).shape shouldBe pieceShape.value.coordinates
                }
            }
        }
        "constructed with a set of coordinates" should {
            "be identical to a Piece constructed with the corresponding shape" {
                PieceShape.values().forEach {
                    for (trafo in it.variants) {
                        Piece(kind = it, shape = trafo.key) shouldBe
                                Piece(kind = it, rotation = trafo.value.first, isFlipped = trafo.value.second)
                    }
                }
            }
        }
        "placed on a certain position" should {
            val position = Coordinates(2, 2)
            val coordinates = setOf(Coordinates(2, 3), Coordinates(3, 3), Coordinates(3, 2))
            val piece = Piece(Color.RED, PieceShape.TRIO_L, Rotation.NONE, true, position)
            "have its coordinates relative to this position" {
                piece.shape shouldBe PieceShape.TRIO_L.coordinates.flip()
                piece.coordinates shouldBe coordinates
            }
        }
        "converted to String" should {
            "consider all properties" {
                val piece = Piece(Color.YELLOW, PieceShape.TETRO_O, Rotation.RIGHT, false)
                val pieceString = piece.toString()
                listOf(
                        piece.copy(color = Color.RED),
                        piece.copy(kind = PieceShape.TETRO_L),
                        piece.copy(rotation = Rotation.LEFT),
                        piece.copy(isFlipped = true),
                        Piece(Color.RED),
                        Piece(Color.GREEN, PieceShape.TRIO_L, Rotation.NONE, true, Coordinates(5, 9))
                ).forAll {
                    it.toString() shouldNotBe pieceString
                }
            }
        }
        "serialised" should {
            "convert to proper xml" {
                forAll(
                        row(Piece(Color.YELLOW, PieceShape.TETRO_O, Rotation.RIGHT, false), """
                                <piece color="YELLOW" kind="TETRO_O" rotation="RIGHT" isFlipped="false">
                                  <position x="0" y="0"/>
                                </piece>
                                """.trimIndent()),
                        row(Piece(Color.RED, PieceShape.PENTO_Y, Rotation.LEFT, false), """
                                <piece color="RED" kind="PENTO_Y" rotation="LEFT" isFlipped="false">
                                  <position x="0" y="0"/>
                                </piece>
                                """.trimIndent()),
                        row(Piece(Color.BLUE, PieceShape.PENTO_P, Rotation.MIRROR, true), """
                                <piece color="BLUE" kind="PENTO_P" rotation="MIRROR" isFlipped="true">
                                  <position x="0" y="0"/>
                                </piece>
                                """.trimIndent()),
                        row(Piece(Color.GREEN, PieceShape.TRIO_L, Rotation.NONE, true, Coordinates(5, 9)), """
                                <piece color="GREEN" kind="TRIO_L" rotation="NONE" isFlipped="true">
                                  <position x="5" y="9"/>
                                </piece>
                                """.trimIndent())
                ) { piece, xml ->
                    piece.coordinates // trigger lazy variables
                    piece shouldSerializeTo xml
                }
            }
        }
    }
    "PieceShapes" When {
        "transformed" should {
            "convert to new shapes analog to Rotations" {
                for (pair in PieceShape.shapes) {
                    val shape = pair.value.coordinates
                    shape.rotate(Rotation.NONE) shouldBe shape
                    shape.rotate(Rotation.RIGHT).rotate(Rotation.RIGHT) shouldBe shape.rotate(Rotation.MIRROR)
                    shape.rotate(Rotation.MIRROR).rotate(Rotation.MIRROR) shouldBe shape
                    shape.rotate(Rotation.LEFT) shouldBe shape.rotate(Rotation.MIRROR).rotate(Rotation.RIGHT)
                    shape.flip(false) shouldBe shape
                    shape.flip(true).flip() shouldBe shape
                }
            }
        }
        "asked for variants" should {
            "always contain the identity transformation" {
                PieceShape.values().forEach {
                    it.variants shouldContain (it.coordinates to Pair(Rotation.NONE, false))
                }
            }
            "contain all different transformation once" {
                PieceShape.MONO.variants shouldContainExactly mapOf(
                        PieceShape.MONO.coordinates to Pair(Rotation.NONE, false)
                )
                PieceShape.DOMINO.variants shouldContainExactly mapOf(
                        PieceShape.DOMINO.coordinates to Pair(Rotation.NONE, false),
                        PieceShape.DOMINO.transform(Rotation.RIGHT, false) to Pair(Rotation.RIGHT, false)
                )
                PieceShape.TRIO_L.variants shouldContainExactly mapOf(
                        PieceShape.TRIO_L.coordinates to Pair(Rotation.NONE, false),
                        PieceShape.TRIO_L.transform(Rotation.NONE, true) to Pair(Rotation.NONE, true),
                        PieceShape.TRIO_L.transform(Rotation.RIGHT, false) to Pair(Rotation.RIGHT, false),
                        PieceShape.TRIO_L.transform(Rotation.RIGHT, true) to Pair(Rotation.RIGHT, true)
                )
                PieceShape.TETRO_O.variants shouldContainExactly mapOf(
                        PieceShape.TETRO_O.coordinates to Pair(Rotation.NONE, false)
                )
            }
        }
    }
    "Sets of coordinates" When {
        "aligned" should {
            val actual = setOf(Coordinates(1, 2), Coordinates(3, 2)).align()
            val expected = setOf(Coordinates(0, 0), Coordinates(2, 0))
            "align to origin" {
                actual shouldBe expected
            }
        }
        
        val transformations =
                ((Rotation.values() zip List(Rotation.values().size) { false }) +
                 (Rotation.values() zip List(Rotation.values().size) { true }))
        "PENTO_W is transformed" should {
            val shape = PieceShape.PENTO_W
            val shapes = listOf(
                    setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 2)),
                    setOf(Coordinates(0, 2), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 0)),
                    setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 2)),
                    setOf(Coordinates(0, 2), Coordinates(1, 2), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0))
            )
            val expected = (shapes + shapes.asReversed())
            val actual = transformations.map { shape.transform(it.first, it.second) }
            "transform each coordinate linearly" {
                actual shouldBe expected
            }
        }
        "PENTO_R is transformed" should {
            val shape = PieceShape.PENTO_R
            val expected = listOf(
                    setOf(Coordinates(0, 1), Coordinates(1, 2), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0)),
                    setOf(Coordinates(0, 1), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 2)),
                    setOf(Coordinates(0, 2), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 1)),
                    setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 1)),
                    setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2), Coordinates(2, 1)),
                    setOf(Coordinates(0, 2), Coordinates(1, 2), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 1)),
                    setOf(Coordinates(0, 1), Coordinates(1, 0), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 2)),
                    setOf(Coordinates(0, 1), Coordinates(1, 2), Coordinates(1, 1), Coordinates(1, 0), Coordinates(2, 0))
            )
            val actual = transformations.map { shape.transform(it.first, it.second) }
            "transform each coordinate linearly" {
                actual shouldBe expected
            }
        }
        "TETRO_L is transformed" should {
            val shape = PieceShape.TETRO_L
            val expected = listOf(
                    setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2)),
                    setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 0), Coordinates(2, 0)),
                    setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2)),
                    setOf(Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(2, 0)),
                    setOf(Coordinates(1, 0), Coordinates(1, 1), Coordinates(1, 2), Coordinates(0, 2)),
                    setOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(2, 1)),
                    setOf(Coordinates(1, 0), Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2)),
                    setOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1))
            )
            val actual = transformations.map { shape.transform(it.first, it.second) }
            "transform each coordinate linearly" {
                actual shouldBe expected
            }
        }
    }
})