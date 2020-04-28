package sc.shared


import com.thoughtworks.xstream.XStream
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class PlayerScoreTest: StringSpec({
    "check equality" {
        val playerScoreUnknown1 = PlayerScore(ScoreCause.UNKNOWN, "", 0)
        val playerScoreUnknown2 = PlayerScore(ScoreCause.UNKNOWN, "", 0)
        playerScoreUnknown1 shouldBe playerScoreUnknown1
        playerScoreUnknown1 shouldBe playerScoreUnknown2

        val playerScoreRegular = PlayerScore(ScoreCause.REGULAR, "", 0)
        playerScoreUnknown1 shouldNotBe playerScoreRegular
        
        val playerScoreReason = PlayerScore(ScoreCause.UNKNOWN, "different", 0)
        playerScoreUnknown1 shouldNotBe playerScoreReason
        
        val playerScoreScores = PlayerScore(ScoreCause.UNKNOWN, "", 0, 1)
        playerScoreUnknown1 shouldNotBe playerScoreScores
        
        val noPlayerScore: Any? = null
        noPlayerScore shouldNotBe playerScoreUnknown1
    }
    "convert XML" {
        val playerScore = PlayerScore(ScoreCause.REGULAR, "Reason", 0, 1, 2)
        var xstream = XStream()
        xstream.setMode(XStream.NO_REFERENCES)
        xstream.classLoader = PlayerScore::class.java.classLoader
        val ISplayerScoreXML = xstream.toXML(playerScore)
        val SHOULDplayerScoreXML = """
            <sc.shared.PlayerScore>
              <cause>REGULAR</cause>
              <reason>Reason</reason>
              <parts>
                <big-decimal>0</big-decimal>
                <big-decimal>1</big-decimal>
                <big-decimal>2</big-decimal>
              </parts>
            </sc.shared.PlayerScore>""".trimIndent()
        playerScore shouldBe xstream.fromXML(SHOULDplayerScoreXML)
        playerScore shouldBe xstream.fromXML(ISplayerScoreXML)
        ISplayerScoreXML shouldBe SHOULDplayerScoreXML
    }
})