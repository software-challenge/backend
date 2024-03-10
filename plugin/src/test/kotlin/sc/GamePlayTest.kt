package sc

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.iterator.*
import org.slf4j.LoggerFactory
import sc.api.plugins.IGamePlugin
import sc.api.plugins.IGameState
import sc.api.plugins.Team
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.api.plugins.host.IGameListener
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.Constants
import sc.framework.plugins.Player
import sc.shared.GameResult
import sc.shared.PlayerScore
import sc.shared.Violation
import kotlin.time.Duration.Companion.milliseconds

/** This test verifies that the Game implementation can be used to play a game.
 * It is the only plugin-test independent of the season. */
class GamePlayTest: WordSpec({
    val logger = LoggerFactory.getLogger(GamePlayTest::class.java)
    isolationMode = IsolationMode.SingleInstance
    val plugin = IGamePlugin.loadPlugin()
    fun createGame() = plugin.createGame() as AbstractGame
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
            game.onRoundBasedAction(game.currentState.moveIterator().next())
            game.isPaused shouldBe true
        }
    }
    "A Game started with two players" When {
        "played normally" should {
            val game = createGame()
            game.onPlayerJoined().team shouldBe Team.ONE
            game.onPlayerJoined().team shouldBe Team.TWO
            game.start()
            
            var finalState: Int? = null
            game.addGameListener(object: IGameListener {
                override fun onGameOver(result: GameResult) {
                    logger.info("Game over: $result")
                }
                
                override fun onStateChanged(data: IGameState, observersOnly: Boolean) {
                    data.hashCode() shouldNotBe finalState
                    // hashing it to avoid cloning, since we get the original object which might be mutable
                    finalState = data.hashCode()
                    logger.debug("Updating state hash to $finalState")
                }
            })
            
            "finish without issues".config(invocationTimeout = plugin.gameTimeout.milliseconds) {
                while(true) {
                    try {
                        val condition = game.checkWinCondition()
                        if(condition != null) {
                            logger.info("Game ended with $condition")
                            break
                        }
                        
                        val state = game.currentState
                        if(finalState != null)
                            finalState shouldBe state.hashCode()
                        
                        val moves = state.moveIterator()
                        withClue(state) {
                            moves.shouldHaveNext()
                            game.onAction(game.players[state.currentTeam.index], moves.next())
                        }
                    } catch(e: Exception) {
                        logger.warn(e.message)
                        break
                    }
                }
                withClue(game.currentState) {
                    // Note that this fails if the game ends incorrectly
                    game.currentState.isOver.shouldBeTrue()
                }
            }
            "send the final state to listeners" {
                finalState shouldBe game.currentState.hashCode()
            }
            "return regular scores" {
                val result = game.getResult()
                result.isRegular shouldBe true
                val scores = result.scores.values
                scores.first().parts.first().intValueExact() shouldBe when(scores.last().parts.first().intValueExact()) {
                    Constants.LOSE_SCORE -> Constants.WIN_SCORE
                    Constants.WIN_SCORE -> Constants.LOSE_SCORE
                    Constants.DRAW_SCORE -> Constants.DRAW_SCORE
                    else -> throw NoWhenBranchMatchedException()
                }
            }
        }
    }
})
