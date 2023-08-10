package sc.plugin2024

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Acceleration
import sc.shared.InvalidMoveException

class AccelerationTest: FunSpec({
    test("serializes nicely") {
        Acceleration(5) shouldSerializeTo  """<acceleration acc="5"/>"""
    }
    
    val gameState = GameState()
    val ship = Ship(CubeCoordinates.ORIGIN, Team.ONE)
    
    test("Acceleration should correctly update speed and coal of the ship") {
        val acceleration = Acceleration(3)
        ship.coal = 5
        
        acceleration.perform(gameState, ship)
        
        ship.speed shouldBe 4
        ship.movement shouldBe 4
        ship.coal shouldBe 3
        ship.freeAcc shouldBe 0
    }
    
    context("throw InvalidMoveException") {
        test("when accelerating beyond maximum speed") {
            val acceleration = Acceleration(6)
            shouldThrow<InvalidMoveException> { acceleration.perform(gameState, gameState.currentShip) }
        }
        
        test("when decelerating beyond minimum speed") {
            val deceleration = Acceleration(-1)
            shouldThrow<InvalidMoveException> { deceleration.perform(gameState, ship) }
        }
        
        test("when accelerating with insufficient coal") {
            val acceleration = Acceleration(3)
            ship.coal = 1
            shouldThrow<InvalidMoveException> { acceleration.perform(gameState, ship) }
        }
        
        xtest("on a sandbank") {
            ship.position = gameState.board
                    .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
            val acceleration = Acceleration(1)
            shouldThrow<InvalidMoveException> {
                acceleration.perform(gameState, ship)
            }
        }
    }
})