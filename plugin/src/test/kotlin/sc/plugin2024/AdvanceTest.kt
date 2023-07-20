package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Advance
import sc.plugin2024.exceptions.AdvanceException
import sc.shared.InvalidMoveException

class AdvanceTest: FunSpec({
    test("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(Advance::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.actions.*"))
        }
        
        val serialized = xStream.toXML(Advance(5))
        
        serialized shouldBe """<advance distance="5"/>"""
    }
    
    test("valid moves") {
        val ship = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(ship, shipTWO))
        ship.speed = 2
        ship.movement = 2
        
        val validAdvance = Advance(2)
        validAdvance.perform(gameState, ship)
        
        ship.position shouldBe CubeCoordinates(2, -1)
        ship.direction shouldBe CubeDirection.RIGHT
        ship.movement shouldBe 0
        ship.speed shouldBe 2
    }
    
    test("no movement points") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO))
        
        val invalidAdvance = Advance(3)
        
        shouldThrow<InvalidMoveException> {
            invalidAdvance.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.NO_MOVEMENT_POINTS
    }
    
    test("invalid distance") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        shipONE.speed = 2
        shipONE.movement = 6
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO))
        
        val invalidAdvanceLessThanMinusOne = Advance(-2)
        val invalidAdvanceZero = Advance(0)
        val invalidAdvanceMoreThanSix = Advance(7)
        
        shouldThrow<InvalidMoveException> {
            invalidAdvanceLessThanMinusOne.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.INVALID_DISTANCE
        shouldThrow<InvalidMoveException> {
            invalidAdvanceZero.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.INVALID_DISTANCE
        shouldThrow<InvalidMoveException> {
            invalidAdvanceMoreThanSix.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.INVALID_DISTANCE
    }
    
    test("field does not exists") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        shipONE.speed = 2
        shipONE.movement = 2
        shipONE.direction = CubeDirection.LEFT
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO))
        
        val invalidAdvanceLessThanMinusOne = Advance(2)
        
        shouldThrow<InvalidMoveException> {
            invalidAdvanceLessThanMinusOne.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.FIELD_NOT_EXIST
    }
    
    test("field is blocked") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        shipONE.speed = 1
        shipONE.movement = 1
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO))
        val blockedFieldCoordinate = gameState.board
                .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.BLOCKED::class).first()
        
        var takenDirection: CubeDirection? = null
        
        shipONE.position = CubeDirection.values().map { it.vector }.firstOrNull { direction ->
            gameState.board[blockedFieldCoordinate + direction] != null
                    .also { takenDirection = CubeDirection.values().firstOrNull { it.vector == direction } }
        }?.let { blockedFieldCoordinate + it } ?: run {
            throw IllegalStateException("No valid direction found.")
        }
        
        shipONE.direction = takenDirection?.opposite()
                            ?: throw IllegalStateException("No valid opposite direction found.")
        
        val invalidAdvanceLessThanMinusOne = Advance(1)
        
        shouldThrow<InvalidMoveException> {
            invalidAdvanceLessThanMinusOne.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.FIELD_IS_BLOCKED
    }
    
    test("backwards move is not possible") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        shipONE.speed = 1
        shipONE.movement = 1
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO))
        
        val invalidAdvanceLessThanMinusOne = Advance(-1)
        
        shouldThrow<InvalidMoveException> {
            invalidAdvanceLessThanMinusOne.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.BACKWARD_MOVE_NOT_POSSIBLE
    }
    
    test("only one move allowed on sandbank") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        shipONE.speed = 1
        shipONE.movement = 1
        val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO))
        val sandbankCoordinate = gameState.board
                .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
        
        shipONE.position = sandbankCoordinate
        
        shipONE.direction = CubeDirection.values().firstOrNull { direction ->
            val dest = gameState.board[sandbankCoordinate + direction.vector]
            dest != null && dest.isEmpty
        } ?: throw IllegalStateException("No valid direction found.")
        
        val invalidAdvanceLessThanMinusOne = Advance(2)
        
        shouldThrow<InvalidMoveException> {
            invalidAdvanceLessThanMinusOne.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.ONLY_ONE_MOVE_ALLOWED_ON_SANDBANK
    }
    
    test("ship already in target") {
        val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.RIGHT.vector, Team.ONE)
        shipONE.speed = 1
        shipONE.movement = 1
        shipONE.direction = CubeDirection.LEFT
        val shipTWO = Ship(CubeCoordinates.ORIGIN, Team.TWO)
        val gameState = GameState(ships = listOf(shipONE, shipTWO), board = Board(segments = generateBoard().take(2)))
        
        val moveOnOtherShip = Advance(2)
        
        shouldThrow<InvalidMoveException> {
            moveOnOtherShip.perform(gameState, shipONE)
        }.mistake shouldBe AdvanceException.SHIP_ALREADY_IN_TARGET
    }
    
})