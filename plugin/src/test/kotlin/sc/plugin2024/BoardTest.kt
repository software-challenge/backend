package sc.plugin2024

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.string.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2024.util.PluginConstants

class BoardTest: FunSpec({
    context(Segment::class.simpleName!!) {
        test("generates goals") {
            val segment = generateSegment(true, arrayOf())
            segment.sumOf { it.count { it == FieldType.WATER } } shouldBe 17
            forAll(1, 2, 3) {
                segment[PluginConstants.SEGMENT_FIELDS_WIDTH - 1, it] shouldBe FieldType.GOAL
            }
        }
        test("serializes nicely") {
            Segment(CubeDirection.RIGHT, CubeCoordinates(0, 0), arrayOf(arrayOf(FieldType.WATER))) shouldSerializeTo """
              <segment direction="RIGHT">
                <column>
                  <field type="WATER"/>
                </column>
              </segment>
            """
            Segment(CubeDirection.RIGHT, CubeCoordinates(0, 0), arrayOf(arrayOf(FieldType.PASSENGER(CubeDirection.LEFT)))) shouldSerializeTo """
              <segment direction="RIGHT">
                <column>
                  <field type="PASSENGER" direction="LEFT" passenger="1" />
                </column>
              </segment>
            """
            Segment(CubeDirection.DOWN_LEFT, CubeCoordinates(0, 0), arrayOf(arrayOf(FieldType.PASSENGER(CubeDirection.RIGHT, 0), FieldType.WATER), arrayOf(FieldType.SANDBANK, FieldType.GOAL))) shouldSerializeTo """
              <segment direction="DOWN_LEFT">
                <column>
                  <field type="PASSENGER" direction="RIGHT" passenger="0" />
                  <field type="WATER" />
                </column>
                <column>
                  <field type="SANDBANK" />
                  <field type="GOAL" />
                </column>
              </segment>
            """ // Do not serialize center to avoid imposing coordinate system
            // TODO how to serialize ship position?
        }
        // TODO test deep copying
    }
    context(Board::class.simpleName!!) {
        val generatedBoard = Board()
        context("generates properly") {
            forAll<Segment>(generatedBoard.segments.take(2)) {
                it.direction shouldBe CubeDirection.RIGHT
            }
            generatedBoard.segments[1].center shouldBe CubeCoordinates(4, 0)
        }
        test("clones deeply") {
            val board = Board(listOf())
            //board.getPenguins() shouldHaveSize 1
            //val clone = board.clone()
            //board[1 y 1] = Team.ONE
            //board.getPenguins() shouldHaveSize 2
            //clone.getPenguins() shouldHaveSize 1
            //clone shouldBe makeBoard(0 y 0 to 1)
        }
    }
    
    context("get field by CubeCoordinates") {
        val board = Board()
        test("delineates first segment") {
            board[CubeCoordinates(0, 0)] shouldBe FieldType.WATER
            board[CubeCoordinates(-1, -2)] shouldBe FieldType.WATER
            board[CubeCoordinates(-2, -2)].shouldBeNull()
            board[CubeCoordinates(0, -3)].shouldBeNull()
            board.getCoordinateByIndex(0, 0, 0) shouldBe CubeCoordinates(-1, -2)
        }
        test("end of second segment") {
            board[CubeCoordinates(4, 2)].shouldNotBeNull()
            board[CubeCoordinates(6, -2)].shouldNotBeNull()
            board.getCoordinateByIndex(1, 4, 0) shouldBe CubeCoordinates(7, -2)
            board.getCoordinateByIndex(1, 4, 4) shouldBe CubeCoordinates(7, 2)
        }
    }
    
    context("getCoordinateByIndex") {
        val board = Board()
        test("returns correct CubeCoordinates from indexes") {
            val coordinate = board.getCoordinateByIndex(1, 0, 0)
            coordinate shouldBe CubeCoordinates(3, -2, -1)
        }
    }

    context("findSegment") {
        val board = Board()
        test("findSegment should return correct segment index") {
            val coord0 = CubeCoordinates(0, 0, 0)
            val findSegmentMethod = Board::class.java.getDeclaredMethod("findSegment", CubeCoordinates::class.java)
            findSegmentMethod.isAccessible = true

            val result0 = findSegmentMethod.invoke(board, coord0) as Int
            result0 shouldBe 0

            val coord1 = CubeCoordinates(4, 0, -4)
            val result1 = findSegmentMethod.invoke(board, coord1) as Int
            result1 shouldBe 1

            val coord2 = CubeCoordinates(0, -3, 3)
            val result2 = findSegmentMethod.invoke(board, coord2) as Int
            result2.shouldBeNull()
        }
    }
    
    context("segmentDistance") {
        val board = Board()
        test("calculates correct segment distance") {
            val distance = board.segmentDistance(CubeCoordinates(0, 0, 0), CubeCoordinates(4, -4, 0))
            distance shouldBe 0
        }
        test("returns -1 when there is no segment") {
            val distance = board.segmentDistance(CubeCoordinates(0, -3, 3), CubeCoordinates(0, -3, 3))
            distance.shouldBeNull()
        }
    }
    
    context("Board calculates Moves") {
        //val board = makeBoard(0 y 0 to 0)
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
            """
        }
        test("random Board length") {
            testXStream.toXML(Board()) shouldHaveLineCount 82
        }
        test("Board with content") {
            val fieldTwo = "<field>TWO</field>"
            //testXStream.fromXML(fieldTwo) shouldBe Field(penguin = Team.TWO)
            //testXStream.fromXML("<board><list>$fieldTwo</list>") shouldBe Board(listOf(mutableListOf(Field(penguin = Team.TWO))))
            //testXStream.toXML(makeBoard(0 y 0 to 1)) shouldContainOnlyOnce fieldTwo
        }
    }
})