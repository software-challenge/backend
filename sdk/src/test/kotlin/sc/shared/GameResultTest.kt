package sc.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.*
import io.kotest.matchers.string.*
import sc.api.plugins.TestTeam
import sc.framework.plugins.Player
import sc.helpers.shouldSerializeTo

class GameResultTest: WordSpec({
    val definition = ScoreDefinition("winner")
    val scoreRegular = PlayerScore(ScoreCause.REGULAR, "", 1)
    val scores = listOf(scoreRegular, PlayerScore(ScoreCause.LEFT, "Player left", 0))
    val winner = Player(TestTeam.BLUE, "bluez")
    "A PlayerScore" When {
        "provided with a matching ScoreDefinition" should {
            "include that in its stringification" {
                scoreRegular.toString(definition) shouldContain "winner=1"
            }
        }
        "provided with a non-matching ScoreDefinition" should {
            val twoFragmentDefinition = ScoreDefinition("winner", "test")
            "throw an IllegalArgumentException" {
                shouldThrow<IllegalArgumentException> { scoreRegular.toString(twoFragmentDefinition) }
            }
        }
    }
    "GameResult" should {
        val gameResultWinner = GameResult(definition, scores, winner)
        val gameResultNoWinner = GameResult(definition, scores, null)
        "consider the winner in its equality" {
            gameResultWinner shouldNotBe gameResultNoWinner
        }
        "serialize properly to XML".config(false) {
            // FIXME needs https://github.com/software-challenge/backend/issues/295
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
                    row(gameResultWinner, gameResultXMLWinner),
                    row(gameResultNoWinner, gameResultXMLNoWinner)
            ) { result, xml ->
                result shouldSerializeTo xml
            }
        }
    }
})
