package sc.plugin2022

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import sc.api.plugins.Team
import sc.plugin2022.PieceType.*

class PieceTest: FunSpec({
    test("to and from String") {
        forAll(ts = PieceType.values().toList()) {
            Piece.fromString(it.char.toString()) shouldBe Piece(it, Team.ONE)
        }
        forAll(
                "MM" to Piece(Moewe, Team.ONE, 1),
                "R2" to Piece(Robbe, Team.ONE, 2),
                "s2" to Piece(Seestern, Team.TWO, 2),
                "hh" to Piece(Herzmuschel, Team.TWO),
        ) { pair: Pair<String, Piece> ->
            pair.second.toString() shouldBe pair.first
            Piece.fromString(pair.first) shouldBe pair.second
        }
    }
    test("can't move backwards") {
        Piece(Herzmuschel, Team.ONE).possibleMoves shouldContainExactlyInAnyOrder listOf(Vector(1, 1), Vector(-1, 1))
        Piece(Seestern, Team.ONE).possibleMoves shouldContainExactlyInAnyOrder listOf(*Vector.diagonals, Vector(0, 1))
    }
})