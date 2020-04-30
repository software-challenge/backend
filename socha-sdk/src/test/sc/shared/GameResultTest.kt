package sc.shared

import sc.framework.plugins.Player

import com.thoughtworks.xstream.XStream
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class GameResultTest: StringSpec({
    "to string conversion" {
        val definition = ScoreDefinition().apply { add("winner") }
        val scores: List<PlayerScore> = listOf(
                PlayerScore(ScoreCause.REGULAR, "test"),
                PlayerScore(ScoreCause.LEFT, "second test")
        )
        val winners: List<Player>? = listOf(Player(PlayerColor.BLUE, "bluez"))
        val gameResultWithWinner = GameResult(definition, scores, winners)
        val gameResultWithoutWinner = GameResult(definition, scores, emptyList())
        val gameResultWithWinnerString = "Winner: [Player BLUE(bluez)]\nPlayer 0: \nPlayer 1: \n"
        val gameResultWithoutWinnerString = "Winner: []\nPlayer 0: \nPlayer 1: \n"
        gameResultWithWinner.toString() shouldBe gameResultWithWinnerString
        gameResultWithoutWinner.toString() shouldBe gameResultWithoutWinnerString
    }
})
