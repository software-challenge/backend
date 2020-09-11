package sc.plugin2021

import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.Configuration
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameStateTest: StringSpec({
    "GameState starts correctly" {
        val state = GameState()
        
        state.board shouldBe Board()
        
        state.undeployedPieceShapes[Color.BLUE]   shouldBe PieceShape.values().toSet()
        state.undeployedPieceShapes[Color.YELLOW] shouldBe PieceShape.values().toSet()
        state.undeployedPieceShapes[Color.RED]    shouldBe PieceShape.values().toSet()
        state.undeployedPieceShapes[Color.GREEN]  shouldBe PieceShape.values().toSet()
    
        state.deployedPieces[Color.BLUE]   shouldBe mutableListOf<Piece>()
        state.deployedPieces[Color.YELLOW] shouldBe mutableListOf<Piece>()
        state.deployedPieces[Color.RED]    shouldBe mutableListOf<Piece>()
        state.deployedPieces[Color.GREEN]  shouldBe mutableListOf<Piece>()
     
        // TODO: adjust values accordingly
        state.getPointsForPlayer(Team.ONE)  shouldBe -178 // Twice the lowest score, once per color
        state.getPointsForPlayer(Team.TWO)  shouldBe -178
    }
    "GameStates know currently active Color" {
        var colorIter = Color.RED
        val state = GameState(startColor = colorIter)
        
        for (x in 0 until 4) {
            state.currentColor shouldBe colorIter
            state.turn++
            colorIter = colorIter.next
        }
    
        state.currentColor shouldBe  Color.RED
        state.turn++
        state.currentColor shouldBe  Color.GREEN
        state.turn += 2
        state.currentColor shouldBe  Color.YELLOW
    }
    "Pieces can only be placed once" {
        val state = GameState(startPiece = PieceShape.PENTO_I)
        val move = SetMove(
                Piece(Color.BLUE, PieceShape.PENTO_I, Rotation.RIGHT, true))
        
        state.undeployedPieceShapes.getValue(Color.BLUE).size shouldBe 21
        state.deployedPieces.getValue(Color.BLUE).size shouldBe 0
        assertDoesNotThrow {
            GameRuleLogic.performMove(state, move)
        }
        state.undeployedPieceShapes.getValue(Color.BLUE).size shouldBe 20
        state.deployedPieces.getValue(Color.BLUE).size shouldBe 1
        state.deployedPieces.getValue(Color.BLUE)[0] shouldBe move.piece
        
        state.turn += 4
        assertThrows<InvalidMoveException> {
            GameRuleLogic.performMove(state, move)
        }
        state.undeployedPieceShapes.getValue(Color.BLUE).size shouldBe 20
        state.deployedPieces.getValue(Color.BLUE).size shouldBe 1
        state.deployedPieces.getValue(Color.BLUE)[0] shouldBe move.piece
        
    }
    "XML conversion works" {
        val xstream = Configuration.xStream
        val state = GameState()
    
        xstream.fromXML(xstream.toXML(state)).toString() shouldBe state.toString()
        xstream.fromXML(xstream.toXML(state))            shouldBe state
        
        val transformed = xstream.fromXML(xstream.toXML(GameState())) as GameState
        transformed.deployedPieces shouldBe null
        GameRuleLogic.isFirstMove(transformed) shouldBe true
        transformed.getPointsForPlayer(Team.ONE)
    }
    "GameStates advance accordingly" {
        var state = GameState(startTurn = 2)
        state.turn shouldBe 2
        state.round shouldBe 1
        state.currentColor shouldBe Color.RED
        
        state = GameState()
        state.turn shouldBe 0
        state.round shouldBe 1
        state.currentColor shouldBe Color.BLUE
    
        state.turn +=10
        state.turn shouldBe 10
        state.round shouldBe 3
        state.currentColor shouldBe Color.RED
    
        state.turn++
        state.turn shouldBe 11
        state.round shouldBe 3
        state.currentColor shouldBe Color.GREEN
    
        state.turn++
        state.turn shouldBe 12
        state.round shouldBe 4
        state.currentColor shouldBe Color.BLUE
    }
})