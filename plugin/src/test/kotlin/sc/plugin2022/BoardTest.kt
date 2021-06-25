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
import io.kotest.matchers.string.shouldMatch
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2022.PieceType.*
import sc.plugin2022.util.Constants
import sc.plugin2022.util.MoveMistake
import sc.shared.InvalidMoveException

class BoardTest: FunSpec({
    context("Board generation") {
        val generatedBoard = Board()
        test("does not misplace pieces") {
            generatedBoard shouldHaveSize Constants.BOARD_SIZE * 2
            generatedBoard.keys.forAll {
                it.x shouldBeOneOf listOf(0, Constants.BOARD_SIZE - 1)
            }
            generatedBoard.values shouldContainExactlyInAnyOrder values().flatMap { type ->
                Team.values().map { team ->
                    Piece(type, team)
                }
            }.let { it + it }
        }
        test("is stringified apropriately") {
            val string = generatedBoard.toString()
            string shouldHaveLineCount 8
            val lineRegex = Regex("\\w\\w------------\\w\\w")
            val lines = string.lines()
            lines.forAll { it shouldMatch lineRegex }
            lines.joinToString("") { it.substring(0, 2).toLowerCase() }.reversed() shouldBe lines.joinToString("") { it.takeLast(2) }
        }
        test("clones well") {
            val board = makeBoard(0 y 0 to "R", 1 y 2 to "m")
            board shouldHaveSize 2
            val clone = board.clone()
            board.movePiece(Move(0 y 0, 1 y 2))
            board shouldHaveSize 1
            clone shouldHaveSize 2
            clone shouldBe makeBoard(0 y 0 to "R", 1 y 2 to "m")
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
            context("from position") {
                test("not when reaching target line of opponent") {
                    val board = makeBoard(6 y 0 to "m")
                    board.movePiece(Move(6 y 0, 7 y 0)) shouldBe 0
                    board shouldHaveSize 1
                }
                test("moewe") {
                    val board = makeBoard(6 y 0 to "M")
                    board.movePiece(Move(6 y 0, 7 y 0)) shouldBe 1
                    board.shouldBeEmpty()
                }
                test("not for Robbe") {
                    val board = makeBoard(6 y 0 to "R")
                    board.movePiece(Move(6 y 0, 7 y 2)) shouldBe 0
                    board shouldHaveSize 1
                }
            }
            context("from tower") {
                val board = makeBoard(1 y 0 to "M", 0 y 0 to "S2", 0 y 1 to "m", 1 y 1 to "r")
                test("not onto own") {
                    shouldThrow<InvalidMoveException> {
                        board.movePiece(Move(0 y 0, 1 y 0))
                    }.mistake shouldBe MoveMistake.DESTINATION_BLOCKED
                }
                test("move tower") {
                    board.movePiece(Move(0 y 0, 1 y 1)) shouldBe 1
                }
                test("move onto tower") {
                    board.movePiece(Move(0 y 1, 0 y 0)) shouldBe 2
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
            move.from shouldBe (0 y 0)
            move.to.isValid.shouldBeFalse()
        }
    }
    context("XML Serialization") {
        test("empty Board") {
            Board(HashMap()) shouldSerializeTo """
              <board>
                <pieces/>
              </board>
            """.trimIndent()
        }
        test("random Board") {
            testXStream.toXML(Board()) shouldHaveLineCount 68
        }
        test("filled Board") {
            makeBoard(0 y 0 to "r", 5 y 6 to "M", 3 y 4 to "R2") shouldSerializeTo """
              <board>
                <pieces>
                  <entry>
                    <coordinates x="0" y="0"/>
                    <piece type="Robbe" team="TWO" count="1"/>
                  </entry>
                  <entry>
                    <coordinates x="5" y="6"/>
                    <piece type="Moewe" team="ONE" count="1"/>
                  </entry>
                  <entry>
                    <coordinates x="3" y="4"/>
                    <piece type="Robbe" team="ONE" count="2"/>
                  </entry>
                </pieces>
              </board>
            """.trimIndent()
        }
    }
})

infix fun Int.y(other: Int) = Coordinates(this, other)

fun makeBoard(vararg list: Pair<Coordinates, String>) =
        Board(list.associateTo(HashMap()) { it.first to Piece.fromString(it.second) })