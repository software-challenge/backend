package sc.plugin2026

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import io.kotest.matchers.ints.*
import sc.api.plugins.Team
import sc.plugin2026.util.GameRuleLogic

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
            GameRuleLogic.greatestSwarmSize(board, Team.ONE) shouldBe 4
            GameRuleLogic.greatestSwarmSize(board, Team.TWO) shouldBe 2
        }
    }
})
