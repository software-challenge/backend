package sc.shared

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

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
    "XML Serialization" {
        val playerScore = PlayerScore(ScoreCause.REGULAR, "Game ended regularly", 0, 1, 2)
        val xstream = getXStream()
        val playerScoreXML = """
            <score cause="REGULAR" reason="Game ended regularly">
              <part>0</part>
              <part>1</part>
              <part>2</part>
            </score>""".trimIndent()
        val playerScoreToXML = xstream.toXML(playerScore)
        playerScoreToXML shouldBe playerScoreXML
        xstream.fromXML(playerScoreXML) shouldBe playerScore
        xstream.fromXML(playerScoreToXML) shouldBe playerScore
    }
})