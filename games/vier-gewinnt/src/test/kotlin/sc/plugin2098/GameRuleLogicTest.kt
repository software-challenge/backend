package sc.plugin2098

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.plugin2098.util.GameRuleLogic
import sc.shared.MoveMistake

class GameRuleLogicTest : FunSpec({
    
    fun boardWith(vararg fields: Triple<Int, Int, FieldState>): Board {
        val board = Board()
        fields.forEach { (x, y, field) ->
            board[x, y] = field
        }
        return board
    }
    
    context("move validation") {
        test("allows placing a token on the bottom row") {
            val board = Board()
            
            GameRuleLogic.checkMove(board, Move(Coordinates(0, 0))) shouldBe null
            GameRuleLogic.checkMove(board, Move(Coordinates(6, 0))) shouldBe null
        }
        
        test("rejects moves outside the board") {
            val board = Board()
            
            GameRuleLogic.checkMove(board, Move(Coordinates(-1, 0))) shouldBe MoveMistake.DESTINATION_OUT_OF_BOUNDS
            GameRuleLogic.checkMove(board, Move(Coordinates(7, 0))) shouldBe MoveMistake.DESTINATION_OUT_OF_BOUNDS
            GameRuleLogic.checkMove(board, Move(Coordinates(0, -1))) shouldBe MoveMistake.DESTINATION_OUT_OF_BOUNDS
            GameRuleLogic.checkMove(board, Move(Coordinates(0, 6))) shouldBe MoveMistake.DESTINATION_OUT_OF_BOUNDS
        }
        
        test("rejects moves onto occupied fields") {
            val board = boardWith(
                Triple(0, 0, FieldState.RED),
            )
            
            GameRuleLogic.checkMove(board, Move(Coordinates(0, 0))) shouldBe MoveMistake.DESTINATION_BLOCKED
        }
        
        test("rejects moves floating in the air") {
            val board = Board()
            
            GameRuleLogic.checkMove(board, Move(Coordinates(0, 1))) shouldBe Connect4MoveMistake.DESTINATION_IN_AIR
            GameRuleLogic.checkMove(board, Move(Coordinates(3, 4))) shouldBe Connect4MoveMistake.DESTINATION_IN_AIR
        }
        
        test("allows moves stacked on another token") {
            val board = boardWith(
                Triple(0, 0, FieldState.RED),
                Triple(0, 1, FieldState.YELLOW),
            )
            
            GameRuleLogic.checkMove(board, Move(Coordinates(0, 2))) shouldBe null
        }
    }
    
    context("four connected detection") {
        test("detects horizontal four") {
            val board = boardWith(
                Triple(0, 0, FieldState.RED),
                Triple(1, 0, FieldState.RED),
                Triple(2, 0, FieldState.RED),
                Triple(3, 0, FieldState.RED),
            )
            
            GameRuleLogic.is4Connected(board, Team.ONE).shouldBeTrue()
            GameRuleLogic.is4Connected(board, Team.TWO).shouldBeFalse()
        }
        
        test("detects vertical four") {
            val board = boardWith(
                Triple(2, 0, FieldState.YELLOW),
                Triple(2, 1, FieldState.YELLOW),
                Triple(2, 2, FieldState.YELLOW),
                Triple(2, 3, FieldState.YELLOW),
            )
            
            GameRuleLogic.is4Connected(board, Team.TWO).shouldBeTrue()
            GameRuleLogic.is4Connected(board, Team.ONE).shouldBeFalse()
        }
        
        test("detects diagonal four rising to the right") {
            val board = boardWith(
                Triple(0, 0, FieldState.RED),
                Triple(1, 1, FieldState.RED),
                Triple(2, 2, FieldState.RED),
                Triple(3, 3, FieldState.RED),
            )
            
            GameRuleLogic.is4Connected(board, Team.ONE).shouldBeTrue()
            GameRuleLogic.is4Connected(board, Team.TWO).shouldBeFalse()
        }
        
        test("detects diagonal four falling to the right") {
            val board = boardWith(
                Triple(0, 3, FieldState.YELLOW),
                Triple(1, 2, FieldState.YELLOW),
                Triple(2, 1, FieldState.YELLOW),
                Triple(3, 0, FieldState.YELLOW),
            )
            
            GameRuleLogic.is4Connected(board, Team.TWO).shouldBeTrue()
            GameRuleLogic.is4Connected(board, Team.ONE).shouldBeFalse()
        }
        
        test("does not detect four when only three are connected") {
            val board = boardWith(
                Triple(0, 0, FieldState.RED),
                Triple(1, 0, FieldState.RED),
                Triple(2, 0, FieldState.RED),
            )
            
            GameRuleLogic.is4Connected(board, Team.ONE).shouldBeFalse()
        }
        
        test("does not connect through opponent tokens") {
            val board = boardWith(
                Triple(0, 0, FieldState.RED),
                Triple(1, 0, FieldState.RED),
                Triple(2, 0, FieldState.YELLOW),
                Triple(3, 0, FieldState.RED),
                Triple(4, 0, FieldState.RED),
            )
            
            GameRuleLogic.is4Connected(board, Team.ONE).shouldBeFalse()
        }
    }
})