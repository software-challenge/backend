package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GameStateTest: StringSpec({
    "GameState starts correctly" {
        val testGameState = GameState()
        
        testGameState.board shouldBe Board()
        
        testGameState.undeployedPieceShapes[Color.BLUE]   shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.YELLOW] shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.RED]    shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.GREEN]  shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.NONE]   shouldBe emptyList()
    
        testGameState.deployedPieces[Color.BLUE]   shouldBe ArrayList()
        testGameState.deployedPieces[Color.YELLOW] shouldBe ArrayList()
        testGameState.deployedPieces[Color.RED]    shouldBe ArrayList()
        testGameState.deployedPieces[Color.GREEN]  shouldBe ArrayList()
        testGameState.deployedPieces[Color.NONE]   shouldBe ArrayList()
        
        testGameState.getPointsForPlayer(Team.ONE)  shouldBe 2
        testGameState.getPointsForPlayer(Team.TWO)  shouldBe 2
        testGameState.getPointsForPlayer(Team.NONE) shouldBe 1
    }
})