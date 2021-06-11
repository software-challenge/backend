package sc.plugin2022

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldStartWith
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.api.plugins.IGameState
import sc.api.plugins.IMove
import sc.api.plugins.Team
import sc.api.plugins.host.IGameListener
import sc.framework.plugins.Player
import sc.plugin2022.util.Constants
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import java.math.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/** This test verifies that the Game implementation works correctly.
 * It is the only test that should stay between seasons. */
@OptIn(ExperimentalTime::class)
class GameTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    val startGame = {
        val game = Game()
        game.onPlayerJoined().team shouldBe Team.ONE
        game.onPlayerJoined().team shouldBe Team.TWO
        game.start()
        Pair(game, game.currentState)
    }
    "A Game started with two players" When {
        "played normally" should {
            val (game, state) = startGame()
    
            var finalState: Int? = null
            game.addGameListener(object: IGameListener {
                override fun onGameOver(results: Map<Player, PlayerScore>) {
                }
        
                override fun onStateChanged(data: IGameState, observersOnly: Boolean) {
                    data.hashCode() shouldNotBe finalState
                    // hashing it to avoid cloning, since we get the original mutable object
                    finalState = data.hashCode()
                }
            })
    
            "finish without issues".config(invocationTimeout = Constants.GAME_TIMEOUT.milliseconds) {
                while (true) {
                    try {
                        val condition = game.checkWinCondition()
                        if (condition != null) {
                            println("Game ended with $condition")
                            break
                        }
                        val moves = state.possibleMoves
                        moves shouldNotBe emptySet<IMove>()
                        game.onAction(game.players[state.currentTeam.index], moves.random())
                    } catch (e: Exception) {
                        println(e.message)
                        break
                    }
                }
            }
            "send the final state to listeners" {
                finalState shouldBe game.currentState.hashCode()
            }
            "return regular scores"  {
                val scores = game.playerScores
                val score1 = game.getScoreFor(game.players.first())
                val score2 = game.getScoreFor(game.players.last())
                scores shouldBe listOf(score1, score2)
                scores.forEach { it.cause shouldBe ScoreCause.REGULAR }
        
                val points1 = BigDecimal(state.getPointsForTeam(Team.ONE))
                val points2 = BigDecimal(state.getPointsForTeam(Team.TWO))
                when {
                    points1 < points2 -> {
                        score1.parts shouldStartWith listOf(BigDecimal(Constants.LOSE_SCORE), points1)
                        score2.parts shouldStartWith listOf(BigDecimal(Constants.WIN_SCORE), points2)
                    }
                    points1 > points2 -> {
                        score1.parts shouldStartWith listOf(BigDecimal(Constants.WIN_SCORE), points1)
                        score2.parts shouldStartWith listOf(BigDecimal(Constants.LOSE_SCORE), points2)
                    }
                    points1 == points2 -> {
                        score1.parts shouldStartWith listOf(BigDecimal(Constants.DRAW_SCORE), points1)
                        score2.parts shouldStartWith listOf(BigDecimal(Constants.DRAW_SCORE), points2)
                    }
                }
            }
        }
    }
})
