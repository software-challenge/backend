package sc.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.*
import io.kotest.matchers.string.*
import sc.api.plugins.Team
import sc.framework.plugins.Player
import sc.helpers.shouldSerializeTo

class GameResultTest: WordSpec({
    val definition = ScoreDefinition("winner")
    val scoreRegular = PlayerScore(ScoreCause.REGULAR, "", 1)
    val scores = mapOf(Player(Team.ONE, "rad") to scoreRegular, Player(Team.TWO, "blues") to PlayerScore(ScoreCause.LEFT, "Player left", 0))
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
        val gameResultWinner = GameResult(definition, scores, Team.ONE)
        val gameResultNoWinner = GameResult(definition, scores, null)
        "consider the winner in its equality" {
            gameResultWinner shouldNotBe gameResultNoWinner
        }
        "serialize properly to XML" {
            val gameResultXML = """
                <result>
                  <definition>
                    <fragment name="winner">
                      <aggregation>SUM</aggregation>
                      <relevantForRanking>true</relevantForRanking>
                    </fragment>
                  </definition>
                  <scores>
                    <entry>
                      <player name="rad" team="ONE"/>
                      <score cause="REGULAR" reason="">
                        <part>1</part>
                      </score>
                    </entry>
                    <entry>
                      <player name="blues" team="TWO"/>
                      <score cause="LEFT" reason="Player left">
                        <part>0</part>
                      </score>
                    </entry>
                  </scores>
                </result>""".trimIndent()
            forAll(
                    row(gameResultWinner, gameResultXML.replace("</scores>", "</scores>\n  <winner team=\"ONE\"/>")),
                    row(gameResultNoWinner, gameResultXML)
            ) { result, xml ->
                result shouldSerializeTo xml
            }
        }
    }
})
