package sc.plugin2021

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.api.plugins.exceptions.GameLogicException
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import java.math.BigDecimal

class GameTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    val startGame = {
        val game = Game()
        game.onPlayerJoined().color shouldBe Team.ONE
        game.onPlayerJoined().color shouldBe Team.TWO
        game.start()
        Pair(game, game.currentState)
    }
    "A Game start with two players" When {
        "played normally" should {
            val (game, state) = startGame()
            "finish without issues" {
                while (true) {
                    try {
                        val condition = game.checkWinCondition()
                        if (condition != null) {
                            println("Game ended with $condition")
                            break
                        }
                        val moves = GameRuleLogic.getPossibleMoves(state)
                        moves shouldNotBe emptySet<SetMove>()
                        game.onAction(state.currentPlayer, moves.random())
                    } catch (e: Exception) {
                        println(e.message)
                        break
                    }
                }
            }
            "return regular scores"  {
                val scores = game.playerScores
                val score1 = game.getScoreFor(game.players.first())
                val score2 = game.getScoreFor(game.players.last())
                scores shouldBe listOf(score1, score2)
                scores.forEach { it.cause shouldBe ScoreCause.REGULAR }
        
                val points1 = BigDecimal(state.getPointsForPlayer(Team.ONE))
                val points2 = BigDecimal(state.getPointsForPlayer(Team.TWO))
                when {
                    points1 < points2 -> {
                        score1.parts shouldBe listOf(BigDecimal(Constants.LOSE_SCORE), points1)
                        score2.parts shouldBe listOf(BigDecimal(Constants.WIN_SCORE), points2)
                    }
                    points1 > points2 -> {
                        score1.parts shouldBe listOf(BigDecimal(Constants.WIN_SCORE), points1)
                        score2.parts shouldBe listOf(BigDecimal(Constants.LOSE_SCORE), points2)
                    }
                    points1 == points2 -> {
                        score1.parts shouldBe listOf(BigDecimal(Constants.DRAW_SCORE), points1)
                        score2.parts shouldBe listOf(BigDecimal(Constants.DRAW_SCORE), points2)
                    }
                }
            }
        }
        "everyone skips" should {
            val (game, state) = startGame()
            for (s in 0 until 4)
                game.onAction(state.currentPlayer, GameRuleLogic.streamPossibleMoves(state).first())
            
            shouldNotThrowAny {
                while (!game.checkGameOver()) {
                    game.onAction(state.currentPlayer, SkipMove(state.currentColor))
                }
            }
            shouldThrow<GameLogicException> {
                game.onAction(state.currentPlayer, SkipMove(state.currentColor))
            }
    
            "end in a draw" {
                game.playerScores shouldContainExactly List(2) {
                    PlayerScore(ScoreCause.REGULAR, "", Constants.DRAW_SCORE, 10)
                }
            }
        }
    }
})
