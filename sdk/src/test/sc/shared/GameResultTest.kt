package sc.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import sc.api.plugins.TestTeam
import sc.framework.plugins.Player

class GameResultTest: StringSpec({
    val definition = ScoreDefinition("winner")
    val scoreRegular = PlayerScore(ScoreCause.REGULAR, "", 1)
    val scores = listOf(scoreRegular, PlayerScore(ScoreCause.LEFT, "Player left", 0))
    val winners = listOf(Player(TestTeam, "bluez"))
    
    "PlayerScore toString with ScoreDefinition" {
        scoreRegular.toString(definition) shouldContain "winner=1"
        val definition2 = ScoreDefinition("winner", "test")
        shouldThrow<IllegalArgumentException> { scoreRegular.toString(definition2) }
    }
    
    val gameResultWinners = GameResult(definition, scores, winners)
    val gameResultWinnersEmpty = GameResult(definition, scores, emptyList())
    val gameResultWinnersNull = GameResult(definition, scores, null)
    "equality" {
        gameResultWinners shouldNotBe gameResultWinnersEmpty
        gameResultWinnersEmpty shouldBe gameResultWinnersNull
        gameResultWinnersEmpty.hashCode() shouldBe gameResultWinnersNull.hashCode()
    }
    "GameResult XML" {
        val xstream = getXStream()
        
        val gameResultXMLWinner = """
                <result>
                  <definition>
                    <fragment name="winner">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <score cause="REGULAR" reason="">
                    <part>1</part>
                  </score>
                  <score cause="LEFT" reason="Player left">
                    <part>0</part>
                  </score>
                  <winner color="BLUE" displayName="bluez"/>
                </result>""".trimIndent()
        val gameResultXMLNoWinner = """
                <result>
                  <definition>
                    <fragment name="winner">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <score cause="REGULAR" reason="">
                    <part>1</part>
                  </score>
                  <score cause="LEFT" reason="Player left">
                    <part>0</part>
                  </score>
                </result>""".trimIndent()
        forAll(
                row(gameResultWinners, gameResultXMLWinner),
                row(gameResultWinnersEmpty, gameResultXMLNoWinner),
                row(gameResultWinnersNull, gameResultXMLNoWinner)
        )
        { result, xml ->
            val toXML = xstream.toXML(result)
            toXML shouldBe xml
            xstream.fromXML(xml) shouldBe result
            xstream.fromXML(toXML) shouldBe result
        }
    }
})
