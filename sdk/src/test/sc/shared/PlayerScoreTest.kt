package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.StringSpec
import sc.helpers.xStream

class PlayerScoreTest: StringSpec({
    "check equality" {
        val playerScoreUnknown1 = PlayerScore(ScoreCause.UNKNOWN, "", 0)
        playerScoreUnknown1 shouldBe playerScoreUnknown1
        val playerScoreRegular = PlayerScore(ScoreCause.REGULAR, "", 0)
        playerScoreUnknown1 shouldNotBe playerScoreRegular
        val playerScoreReason = PlayerScore(ScoreCause.UNKNOWN, "different", 0)
        playerScoreUnknown1 shouldNotBe playerScoreReason
        val playerScoreScores = PlayerScore(ScoreCause.UNKNOWN, "", 0, 1)
        playerScoreUnknown1 shouldNotBe playerScoreScores
        playerScoreScores shouldBe playerScoreScores
    }
    "convert XML" {
        val playerScore = PlayerScore(ScoreCause.REGULAR, "Game ended regularly", 0, 1, 2)
        val playerScoreXML = """
            <score cause="REGULAR" reason="Game ended regularly">
              <part>0</part>
              <part>1</part>
              <part>2</part>
            </score>""".trimIndent()
        val playerScoreToXML = xStream.toXML(playerScore)
        playerScoreToXML shouldBe playerScoreXML
        xStream.fromXML(playerScoreXML) shouldBe playerScore
        xStream.fromXML(playerScoreToXML) shouldBe playerScore
    }
})