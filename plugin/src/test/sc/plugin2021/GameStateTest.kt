package sc.plugin2021

import com.thoughtworks.xstream.XStream
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.plugin2021.util.Configuration

// TODO: add more extensive tests with different GameStates
class GameStateTest: StringSpec({
    "GameState starts correctly" {
        val testGameState = GameState()
        
        testGameState.board shouldBe Board()
        
        testGameState.undeployedPieceShapes[Color.BLUE]   shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.YELLOW] shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.RED]    shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.GREEN]  shouldBe PieceShape.shapes
        testGameState.undeployedPieceShapes[Color.NONE]   shouldBe emptyList<Pair<Int, PieceShape>>()
    
        testGameState.deployedPieces[Color.BLUE]   shouldBe mutableListOf<Piece>()
        testGameState.deployedPieces[Color.YELLOW] shouldBe mutableListOf<Piece>()
        testGameState.deployedPieces[Color.RED]    shouldBe mutableListOf<Piece>()
        testGameState.deployedPieces[Color.GREEN]  shouldBe mutableListOf<Piece>()
        testGameState.deployedPieces[Color.NONE]   shouldBe emptyList<Piece>()
     
        // TODO: adjust values accordingly
        testGameState.getPointsForPlayer(Team.ONE)  shouldBe 2
        testGameState.getPointsForPlayer(Team.TWO)  shouldBe 2
        testGameState.getPointsForPlayer(Team.NONE) shouldBe 1
    }
    "XML conversion works" {
        val xstream = Configuration.xStream
        val state = GameState()
    
        xstream.fromXML(xstream.toXML(state)).toString() shouldBe state.toString()
        xstream.fromXML(xstream.toXML(state))            shouldBe state
    }
})