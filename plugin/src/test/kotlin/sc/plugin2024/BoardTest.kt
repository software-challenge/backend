package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.*
import io.kotest.matchers.string.*
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.y

class BoardTest: FunSpec({
    context(Tile::class.simpleName!!)  {
        //test()
    }
    context(Board::class.simpleName!!) {
        val generatedBoard = Board()
        test("generates properly") {
            generatedBoard.tiles
        }
        test("clones well") {
            val board = Board(listOf())
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
    }
    context("Board calculates diffs") {
        // TODO
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
              <board/>
            """.trimIndent()
        }
        test("random Board length") {
            testXStream.toXML(Board()) shouldHaveLineCount 82
        }
        test("Board with content") {
            val fieldTwo = "<field>TWO</field>"
            testXStream.fromXML(fieldTwo) shouldBe Field(penguin = Team.TWO)
            testXStream.fromXML("<board><list>$fieldTwo</list>") shouldBe Board(listOf(mutableListOf(Field(penguin = Team.TWO))))
            testXStream.toXML(makeBoard(0 y 0 to 1)) shouldContainOnlyOnce fieldTwo
        }
    }
})