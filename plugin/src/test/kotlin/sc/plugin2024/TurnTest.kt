package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.TurnProblem

class TurnTest: FunSpec({
    val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
    val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
    val gameState = GameState(ships = listOf(shipONE, shipTWO))
    
    test("serializes nicely") {
        Turn(CubeDirection.UP_RIGHT) shouldSerializeTo  """<turn direction="UP_RIGHT"/>"""
    }
    
    context("Perform turn actions") {
        xtest("Rotation on SANDBANK should not allowed") {
            shipONE.position = gameState.board
                    .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
            Turn(CubeDirection.UP_RIGHT).perform(gameState) shouldBe TurnProblem.ROTATION_ON_SANDBANK_NOT_ALLOWED
        }
        
        test("Not enough COAL for rotation should not allowed") {
            shipONE.coal = 0
            Turn(CubeDirection.UP_LEFT).perform(gameState) shouldBe TurnProblem.NOT_ENOUGH_COAL_FOR_ROTATION
        }
        
        test("Successful turn action should update direction") {
            shipONE.coal = 5
            shipONE.freeTurns = 2
            val previousDirection = shipONE.direction
            Turn(CubeDirection.UP_LEFT).perform(gameState)
            shipONE.direction shouldNotBe previousDirection
        }
    }
})