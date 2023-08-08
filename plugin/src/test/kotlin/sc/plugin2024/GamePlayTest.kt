package sc.plugin2024

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.booleans.*
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.slf4j.LoggerFactory
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.api.plugins.Team
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.api.plugins.host.IGameListener
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.Constants
import sc.framework.plugins.Player
import sc.plugin2024.util.GamePlugin
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/** This test verifies that the Game implementation can be used to play a game.
 * It is the only test that should stay between seasons. */
@OptIn(ExperimentalTime::class)
class GamePlayTest: WordSpec({
    val logger = LoggerFactory.getLogger(GamePlayTest::class.java)
    isolationMode = IsolationMode.SingleInstance
    fun createGame() = IGamePlugin.loadPlugin().createGame() as AbstractGame
    "A Game" should {
        val game = createGame()
        "let players join" {
            game.onPlayerJoined()
            game.onPlayerJoined()
        }
        "throw on third player join" {
            shouldThrow<TooManyPlayersException> {
                game.onPlayerJoined()
            }
        }
        "set activePlayer on start" {
            game.start()
            game.activePlayer shouldNotBe null
        }
        "stay paused after move" {
            game.isPaused = true
            game.onRoundBasedAction(game.currentState.getSensibleMoves().first())
            game.isPaused shouldBe true
        }
    }
    val startGame = {
        val game = createGame()
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
                    logger.info("Game over: $results")
                }
                
                override fun onStateChanged(data: IGameState, observersOnly: Boolean) {
                    data.hashCode() shouldNotBe finalState
                    // hashing it to avoid cloning, since we get the original mutable object
                    finalState = data.hashCode()
                    logger.debug("Updating state to $finalState")
                }
            })
            
            "finish without issues".config(invocationTimeout = Duration.milliseconds(GamePlugin().gameTimeout)) {
                while (true) {
                    try {
                        val condition = game.checkWinCondition()
                        if (condition != null) {
                            logger.info("Game ended with $condition")
                            break
                        }
                        
                        if(finalState != null)
                            finalState shouldBe state.hashCode()
                        
                        val moves = state.getSensibleMoves()
                        moves.shouldNotBeEmpty()
                        game.onAction(game.players[state.currentTeam.index], moves.random())
                    } catch (e: Exception) {
                        logger.warn(e.message)
                        break
                    }
                }
                game.currentState.isOver.shouldBeTrue()
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
                
                score2.parts.first().intValueExact() shouldBe when (score1.parts.first().intValueExact()) {
                    Constants.LOSE_SCORE -> Constants.WIN_SCORE
                    Constants.WIN_SCORE -> Constants.LOSE_SCORE
                    Constants.DRAW_SCORE -> Constants.DRAW_SCORE
                    else -> throw NoWhenBranchMatchedException()
                }
            }
        }
    }
})
