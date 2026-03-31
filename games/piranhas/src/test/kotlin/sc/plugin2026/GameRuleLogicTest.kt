package sc.plugin2026

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.ints.*
import io.kotest.matchers.maps.*
import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.Team
import sc.plugin2026.util.GameRuleLogic
import sc.shared.MoveMistake

class GameRuleLogicTest: FunSpec({
    context("swarm size") {
        test("generated board") {
            val newBoard = Board()
            Team.values().forAll { team ->
                GameRuleLogic.greatestSwarmSize(newBoard, team) shouldBeGreaterThanOrEqual 8
                GameRuleLogic.isSwarmConnected(newBoard, team) shouldBe false
            }
        }
        test("board of only red fishes") {
            val board = Board(arrayOf(arrayOf(FieldState.ONE_S, FieldState.ONE_M, FieldState.ONE_L)))
            GameRuleLogic.isSwarmConnected(board, Team.ONE) shouldBe true
            GameRuleLogic.isSwarmConnected(board, Team.TWO) shouldBe false
            GameRuleLogic.greatestSwarmSize(board, Team.ONE) shouldBe 6
            GameRuleLogic.greatestSwarmSize(board, Team.TWO) shouldBe -1
        }
        test("board with red and blue fishes") {
            val board = Board(arrayOf(arrayOf(FieldState.ONE_S, FieldState.ONE_L, FieldState.TWO_M, FieldState.ONE_L)))
            GameRuleLogic.isSwarmConnected(board, Team.ONE) shouldBe false
            GameRuleLogic.isSwarmConnected(board, Team.TWO) shouldBe true
            GameRuleLogic.greatestSwarm(board.fieldsForTeam(Team.ONE))?.shouldHaveSize(2)
            GameRuleLogic.greatestSwarmSize(board, Team.ONE) shouldBe 4
            GameRuleLogic.greatestSwarmSize(board, Team.TWO) shouldBe 2
        }
    }
    context("possible moves") {
        test("squid skip") {
            val board = Board(Board.randomFields())
            board[1, 2] = FieldState.SQUID
            board[2, 3] = FieldState.EMPTY
            GameRuleLogic.possibleMovesFor(board, Coordinates(0, 1)) shouldHaveSize 3
            GameRuleLogic.movementDistance(board, Move(Coordinates(0, 1), Direction.UP_RIGHT)) shouldBe 2
            
            board[8, 2] = FieldState.SQUID
            board[7, 3] = FieldState.EMPTY
            val fish = Coordinates(9, 1)
            GameRuleLogic.checkMove(board, Move(fish, Direction.UP_RIGHT)) shouldBe MoveMistake.DESTINATION_OUT_OF_BOUNDS
            GameRuleLogic.checkMove(board, Move(fish, Direction.UP_LEFT)) shouldBe null
            GameRuleLogic.possibleMovesFor(board, fish) shouldHaveSize 3
        }
    }
})
