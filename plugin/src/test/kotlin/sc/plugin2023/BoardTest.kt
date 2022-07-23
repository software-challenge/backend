package sc.plugin2023

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldMatch
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2023.Move
import sc.api.plugins.Coordinates
import sc.api.plugins.TwoDBoard
import sc.framework.plugins.Constants
import sc.plugin2023.util.PluginConstants
import sc.shared.MoveMistake
import sc.shared.InvalidMoveException

class BoardTest: FunSpec({
    context("Board generation") {
        val generatedBoard = Board()
        test("works properly") {
            generatedBoard shouldHaveSize PluginConstants.BOARD_SIZE * PluginConstants.BOARD_SIZE
            generatedBoard.forAll {
                it.penguin.shouldBeNull()
                it.fish shouldBeInRange 1..3
            }
        }
        test("is stringified apropriately") {
            val string = generatedBoard.toString()
            string shouldHaveLineCount 8
            val lineRegex = Regex("\\w\\w------------\\w\\w")
            val lines = string.lines()
            lines.forAll { it shouldMatch lineRegex }
            lines.joinToString("") { it.substring(0, 2).lowercase() }.reversed() shouldBe lines.joinToString("") { it.takeLast(2) }
        }
        test("clones well") {
            val board = makeBoard(0 y 0 to 2)
            //board shouldHaveSize 2
            val clone = board.clone()
            //board.movePiece(Move(0 y 0, 1 y 2))
            //board shouldHaveSize 1
            //clone shouldHaveSize 2
            clone shouldBe makeBoard(0 y 0 to 2)
        }
    }
    context("Board performs Moves") {
        context("refuses invalid moves") {
            test("can't move backwards or off the fields") {
            }
            test("can't move onto own piece") {
            }
        }
    }
    context("Board calculates diffs") {
        //val board = makeBoard(0 y 0 to "r", 2 y 0 to "r")
        //test("empty for itself") {
        //    board.diff(board).shouldBeEmpty()
        //    board.diff(board.clone()).shouldBeEmpty()
        //    board.clone().diff(board).shouldBeEmpty()
        //}
        //test("one moved and one unmoved piece") {
        //    val move = Move(0 y 0, 2 y 1)
        //    val newBoard = board.clone()
        //    newBoard.movePiece(move)
        //    board.diff(newBoard) shouldContainExactly listOf(move)
        //}
        //test("both pieces moved") {
        //    val newBoard = makeBoard(2 y 1 to "r", 1 y 2 to "r")
        //    board.diff(newBoard) shouldHaveSize 2
        //}
        //test("one piece vanished") {
        //    val newBoard = makeBoard(2 y 0 to "r")
        //    val move = board.diff(newBoard).single()
        //    move.from shouldBe (0 y 0)
        //    move.to.isValid.shouldBeFalse()
        //}
    }
    context("XML Serialization") {
        test("empty Board") {
            Board(emptyList()) shouldSerializeTo """
              <board>
                <pieces/>
              </board>
            """.trimIndent()
        }
        test("random Board") {
            testXStream.toXML(Board()) shouldHaveLineCount 68
        }
        test("filled Board") {
            makeBoard(0 y 0 to 1) shouldSerializeTo """
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

fun makeBoard(vararg list: Pair<Coordinates, Int>) =
    Board(List(PluginConstants.BOARD_SIZE) { MutableList(PluginConstants.BOARD_SIZE) { Field(1) } }).apply {
        list.forEach { set(it.first, Team.values().getOrNull(it.second)) }
    }