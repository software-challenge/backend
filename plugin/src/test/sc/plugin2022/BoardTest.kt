package sc.plugin2022

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.*
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
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
                val board = Board(arrayOf(Seestern, Herzmuschel).flatMap { it.teamPieces() }
                        .mapIndexed { index, piece -> Coordinates(index, 4) to piece }.toMap(HashMap()))
                board.entries.forAll {
                    shouldThrow<InvalidMoveException> {
                        board.movePiece(Move(it.key, it.key.copy(y = if (it.value.team == Team.ONE) 3 else 5)))
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
            test("not for other team") {
                val board = makeBoard(0 y 6 to "m")
                board.movePiece(Move(0 y 6, 0 y 7)) shouldBe 0
                board shouldHaveSize 1
            }
            test("from position") {
                val board = makeBoard(0 y 6 to "M")
                board.movePiece(Move(0 y 6, 0 y 7)) shouldBe 1
                board.shouldBeEmpty()
            }
            test("not from Robbe in position") {
                val board = makeBoard(0 y 6 to "R")
                board.movePiece(Move(0 y 6, 2 y 7)) shouldBe 0
                board shouldHaveSize 1
            }
            context("from tower") {
                val board = makeBoard(0 y 1 to "M", 0 y 0 to "S2", 1 y 0 to "m", 1 y 1 to "r")
                test("not onto own") {
                    shouldThrow<InvalidMoveException> {
                        board.movePiece(Move(0 y 0, 0 y 1))
                    }.mistake shouldBe MoveMistake.DESTINATION_BLOCKED
                }
                test("move tower") {
                    board.movePiece(Move(0 y 0, 1 y 1)) shouldBe 1
                }
                test("move onto tower") {
                    board.movePiece(Move(1 y 0, 0 y 0)) shouldBe 2
                }
            }
        }
    }
    context("Board calculates diffs") {
        val board = makeBoard(0 y 0 to "r", 2 y 0 to "r")
        test("empty for itself") {
            board.diff(board).shouldBeEmpty()
            board.diff(board.clone()).shouldBeEmpty()
            board.clone().diff(board).shouldBeEmpty()
        }
        test("one moved and one unmoved piece") {
            val move = Move(0 y 0, 2 y 1)
            val newBoard = board.clone()
            newBoard.movePiece(move)
            board.diff(newBoard) shouldContainExactly listOf(move)
        }
        test("both pieces moved") {
            val newBoard = makeBoard(2 y 1 to "r", 1 y 2 to "r")
            board.diff(newBoard) shouldHaveSize 2
        }
        test("one piece vanished") {
            val newBoard = makeBoard(2 y 0 to "r")
            val move = board.diff(newBoard).single()
            move.start shouldBe (0 y 0)
            move.destination.isValid.shouldBeFalse()
        }
    }
})

infix fun String.at(pos: Coordinates) = Pair(pos, Piece.fromString(this))

infix fun Int.y(other: Int) = Coordinates(this, other)

fun makeBoard(vararg list: Pair<Coordinates, String>) =
        Board(list.associateTo(HashMap()) { it.first to Piece.fromString(it.second) })