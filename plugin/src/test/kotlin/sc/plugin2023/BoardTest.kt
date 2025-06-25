package sc.plugin2023

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.maps.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.string.*
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2023.util.PenguinConstants
import sc.y

class BoardTest: FunSpec({
    context("Board") {
        val generatedBoard = Board()
        test("generates properly") {
            generatedBoard shouldHaveSize PenguinConstants.BOARD_SIZE * PenguinConstants.BOARD_SIZE
            generatedBoard.iterateFields().asSequence().toList().forAll {
                it.penguin.shouldBeNull()
                it.fish shouldBeInRange 0..4
            }
            
            generatedBoard.getPenguins() shouldHaveSize 0
            generatedBoard[1 y 1] = Team.ONE
            generatedBoard[1 y 1] shouldBe Field(penguin = Team.ONE)
            generatedBoard.getPenguins() shouldHaveSize 1
            
            arrayOf(-1 y 1, -2 y 0, -1 y 3, -1 y 0).forAll {
                generatedBoard.getOrNull(it).shouldBeNull()
            }
            (0 until PenguinConstants.BOARD_SIZE).map { (it * 2) y 2 }.forAll {
                val field = generatedBoard[it]
                field.fish shouldBeInRange 0..4
                generatedBoard[it.x, it.y] shouldBe field
            }
        }
        test("clones well") {
            val board = makeBoard(0 y 0 to 1)
            board.getPenguins() shouldHaveSize 1
            val clone = board.clone()
            board[1 y 1] = Team.ONE
            board.getPenguins() shouldHaveSize 2
            clone.getPenguins() shouldHaveSize 1
            clone shouldBe makeBoard(0 y 0 to 1)
        }
    }
    context("Board calculates Moves") {
        val board = makeBoard(0 y 0 to 0)
        test("many possible moves") {
            // right, right down
            board.possibleMovesFrom(0 y 0) shouldHaveSize 2 * (PenguinConstants.BOARD_SIZE - 1)
        }
        test("restricted moves") {
            board[1 y 1] = Team.ONE
            board.possibleMovesFrom(0 y 0) shouldHaveSize PenguinConstants.BOARD_SIZE - 1
        }
    }
    xcontext("XML Serialization") {
        test("empty Board") {
            Board(arrayOf()) shouldSerializeTo """
              <board/>
            """.trimIndent()
        }
        test("random Board length") {
            testXStream.toXML(Board()) shouldHaveLineCount 82
        }
        test("Board with content") {
            val fieldTwo = "<field>TWO</field>"
            testXStream.fromXML(fieldTwo) shouldBe Field(penguin = Team.TWO)
            testXStream.fromXML("<board><list>$fieldTwo</list>") shouldBe makeSimpleBoard(Field(penguin = Team.TWO))
            testXStream.toXML(makeBoard(0 y 0 to 1)) shouldContainOnlyOnce fieldTwo
        }
    }
})

fun makeSimpleBoard(vararg fields: Field) =
        Board(arrayOf(arrayOf(*fields)))

fun makeBoard(vararg list: Pair<Coordinates, Int>) =
        Board(Array(PenguinConstants.BOARD_SIZE) { Array(PenguinConstants.BOARD_SIZE) { Field(1) } }).apply {
            list.forEach { set(it.first, Team.values().getOrNull(it.second)) }
        }
