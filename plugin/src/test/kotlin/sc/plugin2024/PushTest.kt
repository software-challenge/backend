package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Push
import sc.plugin2024.mistake.PushProblem

class PushTest: FunSpec({
    val pushingShip = Ship(position = CubeCoordinates.ORIGIN, team = Team.ONE, movement = 3)
    val nudgedShip = Ship(position = CubeCoordinates.ORIGIN, team = Team.TWO)
    val gameState = GameState(ships = listOf(pushingShip, nudgedShip))
    
    test("XML Serialization") {
        Push(CubeDirection.UP_RIGHT) shouldSerializeTo """<push direction="UP_RIGHT"/>"""
    }
    
    test("Can not push another player without movement points") {
        pushingShip.movement = 0
        val push = Push(CubeDirection.UP_RIGHT)
        push.perform(gameState) shouldBe PushProblem.MOVEMENT_POINTS_EXCEEDED
    }
    
    test("A player can only push another player if they are on the same field") {
        nudgedShip.position += CubeDirection.LEFT.vector
        Push(CubeDirection.UP_RIGHT).perform(gameState) shouldBe PushProblem.SAME_FIELD_PUSH
    }
    
    test("Cannot push another player onto a non-existing field") {
        pushingShip.direction = CubeDirection.LEFT
        pushingShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
        nudgedShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
        Push(CubeDirection.LEFT).perform(gameState) shouldBe PushProblem.INVALID_FIELD_PUSH
    }
    
    test("Pushing costs the pushing player a movement point") {
        val movementPointsBefore = pushingShip.movement
        Push(CubeDirection.RIGHT).perform(gameState) shouldBe null
        pushingShip.movement shouldBe (movementPointsBefore - 1)
    }
    
    test("Cannot push another player in the opposite direction of its movement") {
        val movementDirection = pushingShip.direction
        val push = Push(movementDirection.opposite())
        push.perform(gameState) shouldBe PushProblem.BACKWARD_PUSHING_RESTRICTED
    }
    
    test("When a nudged player gets pushed, he gets an additional free turn") {
        val push = Push(CubeDirection.UP_RIGHT)
        push.perform(gameState) shouldBe null
        nudgedShip.freeTurns shouldBe 2
    }
    
    xtest("Cannot push another player from a sandbank field") {
        val sandbankField: CubeCoordinates = gameState.board
                .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
        
        val validPushDirection: CubeDirection = CubeDirection.values().firstOrNull { direction ->
            val dest = gameState.board[sandbankField + direction.vector]
            dest != null && dest.isEmpty
        } ?: throw IllegalStateException("No valid direction found.")
        
        
        pushingShip.position = sandbankField
        nudgedShip.position = pushingShip.position
        
        Push(validPushDirection).perform(gameState) shouldBe PushProblem.SANDBANK_PUSH
    }
    
    xtest("When a nudged player is pushed onto a sandbank, his speed and movement are set to one") {
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
        
        Push(pushDirection).perform(gameState) shouldBe null
        nudgedShip.speed shouldBe 1
        nudgedShip.movement shouldBe 1
    }
    
    test("Player position on game board changes after being pushed") {
        val initialPosition = nudgedShip.position
        Push(CubeDirection.UP_RIGHT).perform(gameState) shouldBe null
        nudgedShip.position shouldNotBe initialPosition
    }
    
})