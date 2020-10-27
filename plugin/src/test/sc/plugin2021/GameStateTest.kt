package sc.plugin2021

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import sc.plugin2021.util.Constants
import sc.plugin2021.GamePlugin.Companion.loadXStream
import sc.plugin2021.util.GameRuleLogic
import sc.shared.InvalidMoveException

class GameStateTest: StringSpec({
    "GameState starts correctly" {
        val state = GameState()
        
        state.board shouldBe Board()
        
        Color.values().forEach { color ->
            state.undeployedPieceShapes(color)  shouldBe PieceShape.values().toSet()
            state.deployedPieces[color]!!.should(beEmpty())
        }
        
        // TODO: adjust values accordingly
        state.getPointsForPlayer(Team.ONE) shouldBe 0
        state.getPointsForPlayer(Team.TWO) shouldBe 0
    }
    "GameStates know currently active Color" {
        var colorIter = Color.RED
        val state = GameState(startColor = colorIter)
        
        state.orderedColors.size shouldBe Constants.COLORS
        for (x in 0 until Constants.COLORS) {
            state.currentColor shouldBe colorIter
            state.turn++
            colorIter = colorIter.next
        }
        
        state.currentColor shouldBe Color.RED
        state.turn++
        state.currentColor shouldBe Color.GREEN
        state.turn += 2
        state.currentColor shouldBe Color.YELLOW
    }
    "Pieces can only be placed once" {
        val state = GameState(startPiece = PieceShape.PENTO_I)
        val move = SetMove(
                Piece(Color.BLUE, PieceShape.PENTO_I, Rotation.RIGHT, true))
        
        state.undeployedPieceShapes(Color.BLUE).size shouldBe 21
        state.deployedPieces.getValue(Color.BLUE).size shouldBe 0
        assertDoesNotThrow {
            GameRuleLogic.performMove(state, move)
        }
        state.undeployedPieceShapes(Color.BLUE).size shouldBe 20
        state.deployedPieces.getValue(Color.BLUE).size shouldBe 1
        state.deployedPieces.getValue(Color.BLUE)[0] shouldBe move.piece
        
        state.turn += 4
        assertThrows<InvalidMoveException> {
            GameRuleLogic.performMove(state, move)
        }
        state.undeployedPieceShapes(Color.BLUE).size shouldBe 20
        state.deployedPieces.getValue(Color.BLUE).size shouldBe 1
        state.deployedPieces.getValue(Color.BLUE)[0] shouldBe move.piece
        
    }
    "XML conversion works" {
        val state = GameState()
        val transformed = loadXStream().fromXML(loadXStream().toXML(GameState(startPiece = state.startPiece))) as GameState
        transformed.toString() shouldBe state.toString()
        transformed shouldBe state
        
        GameRuleLogic.isFirstMove(transformed) shouldBe true
        transformed.deployedPieces shouldBe null
        transformed.getPointsForPlayer(Team.ONE)
        transformed.board.isEmpty()
    }
    "GameStates advance accordingly" {
        forAll(
                row(2, 1, Color.RED),
                row(7, 2, Color.GREEN),
                row(8, 3, Color.BLUE)
        ) { turn, round, color ->
            val state = GameState(startTurn = turn)
            state.turn shouldBe turn
            state.round shouldBe round
            state.currentColor shouldBe color
        }
    
        GameState().run {
            turn shouldBe 0
            round shouldBe 1
            currentColor shouldBe Color.BLUE
        
            turn += 10
            turn shouldBe 10
            round shouldBe 3
            currentColor shouldBe Color.RED
        
            turn++
            turn shouldBe 11
            round shouldBe 3
            currentColor shouldBe Color.GREEN
        
            turn++
            turn shouldBe 12
            round shouldBe 4
            currentColor shouldBe Color.BLUE
        }
    }
})