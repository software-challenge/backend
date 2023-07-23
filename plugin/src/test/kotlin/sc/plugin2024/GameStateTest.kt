package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team

class GameStateTest: FunSpec({
    val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
    val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
    val gameState = GameState(ships = listOf(shipONE, shipTWO), board = Board(segments = generateBoard().take(2)))
    
    test("gameState should return current ship") {
        gameState.currentShip shouldBe shipTWO
    }
    test("gameState should return other ship") {
        gameState.otherShip shouldBe shipONE
    }
    test("currenTeam getter should work correctly") {
        gameState.currentTeam shouldBe Team.TWO
        gameState.turn = 1
        gameState.currentTeam shouldBe Team.ONE
    }
})