package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.plugin2024.actions.Advance

class GameStateTest: FunSpec({
    val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
    val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
    val gameState = GameState(ships = listOf(shipONE, shipTWO))
    val strippedDownGameState = GameState(ships = listOf(shipONE, shipTWO), board = Board(segments = generateBoard().take(2)))
    
    test("gameState should return current ship") {
        strippedDownGameState.currentShip shouldBe shipTWO
    }
    
    test("gameState should return other ship") {
        strippedDownGameState.otherShip shouldBe shipONE
    }
    
    test("currenTeam getter should work correctly") {
        strippedDownGameState.currentTeam shouldBe Team.TWO
        strippedDownGameState.turn = 1
        strippedDownGameState.currentTeam shouldBe Team.ONE
    }
    
    test("getPossiblePushs should perform correctly") {
        gameState.getPossiblePushs().size shouldBe 0
        
        gameState.currentShip.position = gameState.otherShip.position
        gameState.getPossiblePushs().size shouldNotBe 0
    }
    
    test("getPossibleTurns should perform correctly") {
        gameState.getPossibleTurns(0).size shouldBe 2
        
        gameState.getPossibleTurns(1).size shouldBe 4
    }
    
    test("getPossibleAdvances should perform correctly") {
        gameState.getPossibleAdvances().size shouldBe 1
        
        gameState.currentShip.position = gameState.board
                .findNearestFieldTypes(gameState.currentShip.position, Field.SANDBANK::class).first()
        gameState.getPossibleAdvances() shouldContainAnyOf listOf(Advance(1), Advance(-1))
    }
    
    test("getPossibleAccelerations should perform correctly") {
        gameState.getPossibleAccelerations(0).size shouldBe 1
        
        gameState.getPossibleAccelerations(1).size shouldBe 2
    }
})