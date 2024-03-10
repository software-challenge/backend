package sc.server.gaming

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import sc.server.plugins.TestGame
import sc.shared.Violation

class GameTest: WordSpec({
    "A game" When {
        val game = TestGame()
        val player = game.onPlayerJoined()
        val other = game.onPlayerJoined()
        "one player timed out" Should {
            player.violation = Violation.SOFT_TIMEOUT(3)
            "not be a tie" {
                game.getResult().win?.winner shouldBe other.team
            }
        }
    }
})