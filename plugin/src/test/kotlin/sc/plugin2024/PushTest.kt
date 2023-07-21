package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import org.junit.jupiter.api.Assertions.assertThrows
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.exceptions.PushException
import sc.shared.InvalidMoveException

class PushTest: FunSpec({
    lateinit var gameState: GameState
    lateinit var pushingShip: Ship
    lateinit var nudgedShip: Ship
    
    beforeTest {
        pushingShip = Ship(position = CubeCoordinates.ORIGIN, team = Team.ONE)
        nudgedShip = Ship(position = CubeCoordinates.ORIGIN, team = Team.TWO)
        gameState = GameState(ships = listOf(pushingShip, nudgedShip))
        pushingShip.movement = 3
    }
    
    test("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(Push::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.actions.*"))
        }
        
        val serialized = xStream.toXML(Push(CubeDirection.UP_RIGHT))
        
        serialized shouldBe """<push direction="UP_RIGHT"/>"""
    }
    
    test("Pushing another player requires a direction") {
        val push = Push(CubeDirection.UP_RIGHT)
        push.direction.shouldBe(CubeDirection.UP_RIGHT)
    }
    
    test("Can not push another player without movement points") {
        pushingShip.movement = 0
        val push = Push(CubeDirection.UP_RIGHT)
        shouldThrow<InvalidMoveException> {
            push.perform(gameState, pushingShip)
        }.mistake shouldBe PushException.MOVEMENT_POINTS_EXCEEDED
    }
    
    test("A player can only push another player if they are on the same field") {
        nudgedShip.position += CubeDirection.LEFT.vector
        val push = Push(CubeDirection.UP_RIGHT)
        shouldThrow<InvalidMoveException> {
            push.perform(gameState, pushingShip)
        }.mistake shouldBe PushException.SAME_FIELD_PUSH
    }
    
    test("Cannot push another player onto a non-existing field") {
        pushingShip.direction = CubeDirection.LEFT
        pushingShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
        nudgedShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
        val push = Push(CubeDirection.LEFT)
        shouldThrow<InvalidMoveException> {
            push.perform(gameState, pushingShip)
        }.mistake shouldBe PushException.INVALID_FIELD_PUSH
    }
    
    test("Cannot push another player from a sandbank field") {
        val sandbankField: CubeCoordinates = gameState.board
                .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
        
        val validPushDirection: CubeDirection = CubeDirection.values().firstOrNull { direction ->
            val dest = gameState.board[sandbankField + direction.vector]
            dest != null && dest.isEmpty
        } ?: throw IllegalStateException("No valid direction found.")
        
        
        pushingShip.position = sandbankField
        nudgedShip.position = pushingShip.position
        
        val push = Push(validPushDirection)
        
        shouldThrow<InvalidMoveException> {
            push.perform(gameState, pushingShip)
        }.mistake shouldBe PushException.SANDBANK_PUSH
    }
    
    test("Pushing costs the pushing player a movement point") {
        val movementPointsBefore = pushingShip.movement
        val push = Push(CubeDirection.RIGHT)
        push.perform(gameState, pushingShip)
        pushingShip.movement shouldBe (movementPointsBefore - 1)
    }
    
    test("Cannot push another player in the opposite direction of its movement") {
        val movementDirection = pushingShip.direction
        val push = Push(movementDirection.opposite())
        shouldThrow<InvalidMoveException> {
            push.perform(gameState, pushingShip)
        }
    }
    
    test("When a nudged player gets pushed, he gets an additional free turn") {
        val push = Push(CubeDirection.UP_RIGHT)
        push.perform(gameState, pushingShip)
        nudgedShip.freeTurns shouldBe 1
    }
    
    test("When a nudged player is pushed onto a sandbank, his speed and movement are set to one") {
        val sandbankField: CubeCoordinates = gameState.board
                .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
        
        var takenDirection: CubeDirection? = null
        
        pushingShip.position = CubeDirection.values().map { it.vector }.firstOrNull { direction ->
            gameState.board[sandbankField + direction] != null
                    .also { takenDirection = CubeDirection.values().firstOrNull { it.vector == direction } }
        }?.let { sandbankField + it } ?: run {
            throw IllegalStateException("No valid direction found.")
        }
        
        nudgedShip.position = pushingShip.position
        
        val pushDirection = takenDirection?.opposite()
                            ?: throw IllegalStateException("No valid opposite direction found.")
        
        pushingShip.direction = pushDirection
        
        val push = Push(pushDirection)
        push.perform(gameState, pushingShip)
        nudgedShip.speed shouldBe 1
        nudgedShip.movement shouldBe 1
    }
    
    test("Player position on game board changes after being pushed") {
        val initialPosition = nudgedShip.position
        val push = Push(CubeDirection.UP_RIGHT)
        push.perform(gameState, pushingShip)
        nudgedShip.position shouldNotBe initialPosition
    }
    
})