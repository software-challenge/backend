package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.core.spec.style.StringSpec

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
        val playerScore = PlayerScore(ScoreCause.REGULAR, "Reason", 0, 1, 2)
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
        }
        val playerScoreXML = """
            <sc.shared.PlayerScore>
              <cause>REGULAR</cause>
              <reason>Reason</reason>
              <parts>
                <big-decimal>0</big-decimal>
                <big-decimal>1</big-decimal>
                <big-decimal>2</big-decimal>
              </parts>
            </sc.shared.PlayerScore>""".trimIndent()
        xstream.fromXML(playerScoreXML) shouldBe playerScore
        xstream.toXML(playerScore) shouldBe playerScoreXML
    }
})