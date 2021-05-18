package sc.framework

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.scopes.WordSpecShouldScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.server.plugins.TestGame
import sc.server.plugins.TestMove

suspend inline fun <reified T : Throwable> WordSpecShouldScope.shouldThrowOn(condition: String, crossinline block: () -> Any?){
    "throw ${T::class} on $condition" {
        shouldThrow<T>(block)
    }
}

class GameTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    val game = TestGame()
    "A Game" should {
        "let players join" {
            game.onPlayerJoined()
            game.onPlayerJoined()
        }
        shouldThrowOn<TooManyPlayersException>("third player join") {
            game.onPlayerJoined()
        }
        "set activePlayer on start" {
            game.start()
            game.activePlayer shouldNotBe null
        }
        "stay paused after move" {
            game.isPaused = true
            game.onRoundBasedAction(TestMove(1))
            game.isPaused shouldBe true
        }
    }
})