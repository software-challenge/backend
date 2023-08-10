package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.mistake.AccelerationProblem

class AccelerationTest: FunSpec({
    test("serializes nicely") {
        Acceleration(5) shouldSerializeTo  """<acceleration acc="5"/>"""
    }
    
    val gameState = GameState()
    val ship = gameState.ships.first()
    
    test("Acceleration should correctly update speed and coal of the ship") {
        Acceleration(3).perform(gameState)
        
        ship.speed shouldBe 4
        ship.movement shouldBe 4
        ship.coal shouldBe 4
        ship.freeAcc shouldBe 0
    }
    
    context("detect invalid") {
        test("when accelerating beyond maximum speed") {
            Acceleration(6).perform(gameState) shouldBe AccelerationProblem.ABOVE_MAX_SPEED
        }
        test("when decelerating beyond minimum speed") {
            Acceleration(-1).perform(gameState) shouldBe AccelerationProblem.BELOW_MIN_SPEED
        }
        test("when accelerating with insufficient coal") {
            ship.coal = 1
            Acceleration(3).perform(gameState)
        }
        
        xtest("on a sandbank") {
            ship.position = gameState.board.findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
            Acceleration(1).perform(gameState) shouldBe AccelerationProblem.ON_SANDBANK
        }
    }
})