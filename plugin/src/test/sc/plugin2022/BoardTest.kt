package sc.plugin2022

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeOneOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldNotContain
import sc.api.plugins.Team
import sc.plugin2022.PieceType.*
import sc.plugin2022.util.Constants
import sc.plugin2022.util.MoveMistake
import sc.shared.InvalidMoveException

class BoardTest: FunSpec({
    context("Board generation") {
        val board = Board()
        test("does not misplace pieces") {
            board shouldHaveSize Constants.BOARD_SIZE * 2
            board.keys.forAll {
                it.y shouldBeOneOf listOf(0, Constants.BOARD_SIZE - 1)
            }
            board.values shouldContainExactlyInAnyOrder values().flatMap { type ->
                Team.values().map { team ->
                    Piece(type, team)
                }
            }.let { it + it }
        }
        test("is stringified apropriately") {
            val string = board.toString()
            println(string)
            string shouldHaveLineCount 8
            val lines = string.lines()
            lines.first() shouldNotContain "-"
            lines.last() shouldNotContain "-"
            lines.first().reversed().toLowerCase() shouldBe lines.last()
            lines.subList(1, 7).forAll {
                it shouldBe "----------------"
            }
        }
    }
    context("Board performs Moves") {
        context("refuses invalid moves") {
            test("can't move backwards or off the fields") {
                val board = Board(arrayOf(Seestern, Herzmuschel).flatMap { it.teamPieces() }.mapIndexed { index, piece -> Coordinates(index, 4) to piece }.toMap(HashMap()))
                board.entries.forAll {
                    shouldThrow<InvalidMoveException> {
                        board.movePiece(Move(it.key, it.key.copy(y = if(it.value.team == Team.ONE) 3 else 5)))
                    }.mistake shouldBe MoveMistake.INVALID_MOVEMENT
                }
            }
            test("can't move onto own piece") {
                val board = makeBoard(0 y 0 to "R", 1 y 2 to "R")
                shouldThrow<InvalidMoveException> {
                    board.movePiece(Move(0 y 0, 1 y 2))
                }.mistake shouldBe MoveMistake.DESTINATION_BLOCKED
            }
        }
        context("amber") {
            val coords = Coordinates(0, 6)
            test("not for other team") {
                val moewe = Piece(Moewe, Team.TWO)
                val board = Board(mutableMapOf(coords to moewe))
                board.movePiece(Move(coords, coords.copy(y = 7))) shouldBe moewe
                board.shouldNotBeEmpty()
            }
            test("from position")  {
                val moewe = Piece(Moewe, Team.ONE)
                val board = Board(mutableMapOf(coords to moewe))
                board.movePiece(Move(coords, coords.copy(y = 7))) shouldBe null
                board.shouldBeEmpty()
            }
            test("not from Robbe in position") {
                val robbe = Piece(Robbe, Team.ONE)
                val board = Board(mutableMapOf(coords to robbe))
                board.movePiece(Move(coords, Coordinates(2, 7))) shouldBe robbe
                board.shouldNotBeEmpty()
            }
            test("from tower") {
                val tower = Piece(Herzmuschel, Team.ONE, 2)
                val board = Board(mutableMapOf())
            }
        }
    }
})

infix fun String.at(pos: Coordinates) = Pair(pos, Piece.fromString(this))

infix fun Int.y(other: Int) = Coordinates(this, other)

fun makeBoard(vararg list: Pair<Coordinates, String>) =
        Board(list.associateTo(HashMap()) { it.first to Piece.fromString(it.second) })