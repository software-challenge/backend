package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Advance
import sc.plugin2024.exceptions.AdvanceException
import sc.plugin2024.exceptions.MoveException
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
        Advance(5) shouldSerializeTo """<advance distance="5"/>"""
    }
    
    context("perform") {
        val gameState = GameState()
        val shipONE = gameState.currentShip
        
        test("advance by 2") {
            shipONE.speed = 2
            shipONE.movement = 2
            
            Advance(2).perform(gameState, shipONE)
            
            shipONE.position shouldBe CubeCoordinates(1, -1)
            shipONE.direction shouldBe CubeDirection.RIGHT
            shipONE.movement shouldBe 0
            shipONE.speed shouldBe 2
        }
        
        test("no movement points") {
            shouldThrow<InvalidMoveException> {
                gameState.performMoveDirectly(Move(Advance(3)))
                println(gameState.ships)
            }.mistake shouldBe MoveException.MOVEMENT_POINTS_MISSING
        }
        
        test("invalid distance") {
            shipONE.speed = 2
            shipONE.movement = 6
            
            shouldThrow<InvalidMoveException> {
                Advance(-2).perform(gameState, shipONE)
            }.mistake shouldBe AdvanceException.INVALID_DISTANCE
            shouldThrow<InvalidMoveException> {
                Advance(0).perform(gameState, shipONE)
            }.mistake shouldBe AdvanceException.INVALID_DISTANCE
            shouldThrow<InvalidMoveException> {
                Advance(7).perform(gameState, shipONE)
            }.mistake shouldBe AdvanceException.INVALID_DISTANCE
        }
        
        test("field does not exists") {
            shipONE.speed = 2
            shipONE.movement = 2
            shipONE.direction = CubeDirection.LEFT
            
            val invalidAdvanceLessThanMinusOne = Advance(2)
            
            shouldThrow<InvalidMoveException> {
                invalidAdvanceLessThanMinusOne.perform(gameState, shipONE)
            }.mistake shouldBe AdvanceException.FIELD_IS_BLOCKED
        }
        
        test("field is blocked") {
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
            shipONE.direction = CubeDirection.DOWN_RIGHT
            shouldThrow<InvalidMoveException> {
                Advance(-1).perform(gameState, shipONE)
            }.mistake shouldBe AdvanceException.BACKWARD_MOVE_NOT_POSSIBLE
        }
        
        test("only one move allowed on sandbank") {
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
            shipONE.position = CubeCoordinates(-1, 0)
            shipONE.direction = CubeDirection.DOWN_LEFT
            
            val moveOnOtherShip = Advance(2)
            
            shouldThrow<InvalidMoveException> {
                moveOnOtherShip.perform(gameState, shipONE)
            }.mistake shouldBe AdvanceException.SHIP_ALREADY_IN_TARGET
        }
    }
})