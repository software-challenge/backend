package sc.plugin2024

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.nulls.*
import io.kotest.matchers.string.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2024.util.PluginConstants

class BoardTest: FunSpec({
    context("get field by CubeCoordinates") {
        test("arrayX works within first segment") {
            CubeCoordinates(0, 0, 0).arrayX shouldBe 0 // center
            CubeCoordinates(-3, 2, 1).arrayX shouldBe -1 // bottom left
            CubeCoordinates(0, 2, -2).arrayX shouldBe 2 // bottom right
            CubeCoordinates(1, -2, 1).arrayX shouldBe 1 // top
            val segment = generateSegment(false, arrayOf())
            segment[CubeCoordinates(0, 0)] shouldBe Field.WATER
        }
        val board = Board()
        test("delineates first segment") {
            board.getCoordinateByIndex(0, 0, 0) shouldBe CubeCoordinates(-1, -2)
            board.getCoordinateByIndex(0, 1, 2) shouldBe CubeCoordinates.ORIGIN
            board.getCoordinateByIndex(0, 2, 3) shouldBe CubeCoordinates(0, 1)
            board[CubeCoordinates(0, 0)] shouldBe Field.WATER
            CubeCoordinates(-1, -2).distanceTo(CubeCoordinates.ORIGIN) shouldBe 3
            board[CubeCoordinates(-1, -2)] shouldBe Field.WATER
            board[CubeCoordinates(-3, 2)] shouldBe Field.WATER
            board[CubeCoordinates(-2, -2)].shouldBeNull()
            board[CubeCoordinates(0, -3)].shouldBeNull()
        }
        test("end of second segment") {
            board.getCoordinateByIndex(1,
                    PluginConstants.SEGMENT_FIELDS_WIDTH - 1, 0) shouldBe CubeCoordinates(6, -2)
            board.getCoordinateByIndex(1,
                    PluginConstants.SEGMENT_FIELDS_WIDTH - 1,
                    PluginConstants.SEGMENT_FIELDS_HEIGHT - 1) shouldBe CubeCoordinates(4, 2)
            board[CubeCoordinates(6, -2)] shouldBe Field.WATER // top right
            board[CubeCoordinates(4, 2)] shouldBe Field.WATER // bottom right
        }
        test("start of second segment") {
            val coordinate = board.getCoordinateByIndex(1, 0, 2)
            coordinate shouldBe CubeCoordinates(3, 0)
            val center = board.segments[1].center
            (coordinate + CubeDirection.RIGHT.vector) shouldBe center
            board.getCoordinateByIndex(1, 1, 2) shouldBe center
        }
    }

    context("findSegment") {
        val board = Board()
        test("should return correct segment index") {
            val findSegmentMethod = Board::class.java.getDeclaredMethod("findSegment", CubeCoordinates::class.java)
            findSegmentMethod.isAccessible = true
            findSegmentMethod.invoke(board, CubeCoordinates.ORIGIN) shouldBe 0
            findSegmentMethod.invoke(board, CubeCoordinates(4, 0, -4)) shouldBe 1
            findSegmentMethod.invoke(board, CubeCoordinates(0, -3, 3)).shouldBeNull()
        }
    }
    
    context("segmentDistance") {
        val board = Board()
        test("calculates correct segment distance") {
            board.segmentDistance(CubeCoordinates.ORIGIN, CubeCoordinates(0, 2)) shouldBe 0
            board.segmentDistance(CubeCoordinates.ORIGIN, CubeCoordinates(1, 2)) shouldBe 1
            board.segmentDistance(CubeCoordinates(-1, -2), CubeCoordinates(3, 2)) shouldBe 1
        }
        test("returns null when there is no segment") {
            board.segmentDistance(CubeCoordinates(0, -3, 3), CubeCoordinates(0, -3, 3)).shouldBeNull()
        }
    }
    
    context("find nearest field type") {
        val board = Board()

        test("finds correct nearest specified field type") {
            val startCoordinates = CubeCoordinates(0, 0, 0)
            board.findNearestFieldTypes(startCoordinates, Field.WATER::class) shouldContain CubeCoordinates(1, 0, -1)

            val dynamicField = board.segments.last().center + board.segments.last().direction.vector
            val result = board.findNearestFieldTypes(board.segments.last().center, board[dynamicField]!!::class)
            result shouldContain dynamicField
            
            board.findNearestFieldTypes(startCoordinates, Field.PASSENGER::class).size shouldBeGreaterThanOrEqual 1
        }
    }

    context("pickupPassenger") {
        test("should decrease passenger count of the neighbouring field and increase passenger count of the ship") {
            val ship = Ship(team = Team.ONE, position = CubeCoordinates(0, 0))
            val board = Board()
            val nextPassengerField = board.findNearestFieldTypes(CubeCoordinates(0, 0), Field.PASSENGER::class).first()
            ship.position = nextPassengerField + (board[nextPassengerField] as Field.PASSENGER).direction.vector
            
            val initialShipPassengers: Int = ship.passengers
            val initialFieldPassengers: Int = board.neighboringFields(ship.position)
                                                 .filterIsInstance<Field.PASSENGER>().first().passenger
            
            val isPickedUp = board.pickupPassenger(ship)
            
            isPickedUp shouldBe true
            
            ship.passengers shouldBe initialShipPassengers + initialFieldPassengers
            board.neighboringFields(ship.position)
                    .filterIsInstance<Field.PASSENGER>().first().passenger shouldBe initialFieldPassengers - 1
        }
        
        test("should return false and not change passenger count when no neighbouring passenger fields") {
            val ship = Ship(team = Team.ONE, position = CubeCoordinates(0, 0))
            val board = Board()
            
            val initialShipPassengers = ship.passengers
            
            val isPickedUp = board.pickupPassenger(ship)
            
            isPickedUp shouldBe false
            ship.passengers shouldBe initialShipPassengers
        }
        
    }

    xcontext("Board calculates diffs") {
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
    xtest("clones deeply") {
        val board = Board(listOf())
        //board.getPenguins() shouldHaveSize 1
        //val clone = board.clone()
        //board[1 y 1] = Team.ONE
        //board.getPenguins() shouldHaveSize 2
        //clone.getPenguins() shouldHaveSize 1
        //clone shouldBe makeBoard(0 y 0 to 1)
    }
    xcontext("XML Serialization") {
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