package sc.plugin2099

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.plugin2099.util.GameRuleLogic
import sc.shared.MoveMistake

class GameRuleLogicTest: FunSpec({
    test("possible moves") {
        val board = Board()
        board[1, 2] = FieldState.CROSS
        board[2, 2] = FieldState.CIRCLE
        GameRuleLogic.possibleMoves(board) shouldHaveSize 7

        GameRuleLogic.checkMove(board, Move(Coordinates(1, 2))) shouldBe MoveMistake.DESTINATION_BLOCKED
        GameRuleLogic.checkMove(board, Move(Coordinates(1, 1))) shouldBe null
        GameRuleLogic.checkWinner(board) shouldBe null

        board[1, 1] = FieldState.CROSS
        board[1, 0] = FieldState.CROSS

        GameRuleLogic.checkWinner(board) shouldBe Team.ONE
    }

    test("apply moves") {
        val board = Board()
        board[1, 2] = FieldState.CIRCLE
        board[2, 2] = FieldState.CROSS

        GameRuleLogic.checkMove(board, Move(Coordinates(1, 2))) shouldBe MoveMistake.DESTINATION_BLOCKED
        GameRuleLogic.checkMove(board, Move(Coordinates(1, 1))) shouldBe null
        GameRuleLogic.checkWinner(board) shouldBe null

        board[1, 1] = FieldState.CIRCLE
        board[1, 0] = FieldState.CIRCLE

        GameRuleLogic.checkWinner(board) shouldBe Team.TWO
    }
})