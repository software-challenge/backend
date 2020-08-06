package sc.plugin2021

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.Configuration
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameStateTest: StringSpec({
    "GameState starts correctly" {
        val gameState = GameState()
        
        gameState.board shouldBe Board()
        
        gameState.undeployedPieceShapes[Color.BLUE]   shouldBe PieceShape.values().toSet()
        gameState.undeployedPieceShapes[Color.YELLOW] shouldBe PieceShape.values().toSet()
        gameState.undeployedPieceShapes[Color.RED]    shouldBe PieceShape.values().toSet()
        gameState.undeployedPieceShapes[Color.GREEN]  shouldBe PieceShape.values().toSet()
    
        gameState.deployedPieces[Color.BLUE]   shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.YELLOW] shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.RED]    shouldBe mutableListOf<Piece>()
        gameState.deployedPieces[Color.GREEN]  shouldBe mutableListOf<Piece>()
     
        // TODO: adjust values accordingly
        gameState.getPointsForPlayer(Team.ONE)  shouldBe -178 // Twice the lowest score, once per color
        gameState.getPointsForPlayer(Team.TWO)  shouldBe -178
    }
    "GameStates know currently active Color" {
        var colorIter = Color.RED
        val gameState = GameState(startColor = colorIter)
        
        for (x in 0 until 4) {
            gameState.currentColor shouldBe colorIter
            gameState.turn++
            colorIter = colorIter.next
        }
    
        gameState.currentColor shouldBe  Color.RED
        gameState.turn++
        gameState.currentColor shouldBe  Color.GREEN
        gameState.turn += 2
        gameState.currentColor shouldBe  Color.YELLOW
    }
    "Pieces can only be placed once" {
        val gameState = GameState(startPiece = PieceShape.PENTO_I)
        val move = SetMove(
                Piece(Color.BLUE, PieceShape.PENTO_I, Rotation.RIGHT, true))
        
        gameState.undeployedPieceShapes.getValue(Color.BLUE).size shouldBe 21
        gameState.deployedPieces.getValue(Color.BLUE).size shouldBe 0
        assertDoesNotThrow {
            GameRuleLogic.performMove(gameState, move)
        }
        gameState.undeployedPieceShapes.getValue(Color.BLUE).size shouldBe 20
        gameState.deployedPieces.getValue(Color.BLUE).size shouldBe 1
        gameState.deployedPieces.getValue(Color.BLUE)[0] shouldBe move.piece
        
        gameState.turn += 4
        assertThrows<InvalidMoveException> {
            GameRuleLogic.performMove(gameState, move)
        }
        gameState.undeployedPieceShapes.getValue(Color.BLUE).size shouldBe 20
        gameState.deployedPieces.getValue(Color.BLUE).size shouldBe 1
        gameState.deployedPieces.getValue(Color.BLUE)[0] shouldBe move.piece
        
    }
    "XML conversion works" {
        val xstream = Configuration.xStream
        val state = GameState()
    
        xstream.fromXML(xstream.toXML(state)).toString() shouldBe state.toString()
        xstream.fromXML(xstream.toXML(state))            shouldBe state
    }
    "GameStates advance accordingly" {
        var gameState = GameState(startTurn = 3)
        gameState.turn shouldBe 3
        gameState.round shouldBe 1
        gameState.currentColor shouldBe Color.RED
        
        gameState = GameState()
        gameState.turn shouldBe 1
        gameState.round shouldBe 1
        gameState.currentColor shouldBe Color.BLUE
    
        gameState.turn +=10
        gameState.turn shouldBe 11
        gameState.round shouldBe 3
        gameState.currentColor shouldBe Color.RED
    
        gameState.turn++
        gameState.turn shouldBe 12
        gameState.round shouldBe 3
        gameState.currentColor shouldBe Color.GREEN
    
        gameState.turn++
        gameState.turn shouldBe 13
        gameState.round shouldBe 4
        gameState.currentColor shouldBe Color.BLUE
    }
})