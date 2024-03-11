package sc.server.gaming

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import sc.api.plugins.Team
import sc.server.plugins.TestGame
import sc.server.plugins.TestPlugin
import sc.shared.GameResult
import sc.shared.PlayerScore
import sc.shared.Violation
import sc.shared.WinCondition

class GameTest: WordSpec({
    "A game" When {
        val game = TestGame()
        val player = game.onPlayerJoined()
        player.team shouldBe Team.ONE
        val other = game.onPlayerJoined()
        "one player timed out" Should {
            val violation = Violation.SOFT_TIMEOUT(3)
            player.violation = violation
            "not be a tie" {
                game.getResult() shouldBe GameResult(
                        TestPlugin().scoreDefinition,
                        mapOf(
                                player to PlayerScore(0, 0, 0),
                                other to PlayerScore(2, 1, 0)
                        ),
                        WinCondition(other.team, violation)
                )
            }
        }
    }
})