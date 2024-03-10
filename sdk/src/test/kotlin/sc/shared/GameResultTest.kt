package sc.shared

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.*
import io.kotest.matchers.string.*
import sc.api.plugins.IMove
import sc.api.plugins.Team
import sc.framework.plugins.Player
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream

class GameResultTest: WordSpec({
    val definition = ScoreDefinition("winner")
    val scoreRegular = PlayerScore(1)
    val scores = mapOf(Player(Team.ONE, "rad") to scoreRegular, Player(Team.TWO, "blues") to PlayerScore(0))
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
        val gameResultWinner = GameResult(definition, scores, WinCondition(Team.ONE, WinReason("%s hat gewonnän")))
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
                      <score>
                        <part>1</part>
                      </score>
                    </entry>
                    <entry>
                      <player name="blues" team="TWO"/>
                      <score>
                        <part>0</part>
                      </score>
                    </entry>
                  </scores>
                </result>""".trimIndent()
            forAll(
                    row(gameResultNoWinner, gameResultXML),
                    row(gameResultWinner, gameResultXML.replace("</scores>", "</scores>\n  <winner team=\"ONE\" regular=\"true\" reason=\"rad hat gewonnän\"/>")),
            ) { result, xml ->
                result shouldSerializeTo xml
            }
            testXStream.toXML(GameResult(definition, scores,
                    WinCondition(Team.TWO, Violation.RULE_VIOLATION(InvalidMoveException(object: IMoveMistake {
                        override val message = "bad guy"
                    }))))) shouldBe
                    gameResultXML.replace("</scores>", "</scores>\n  <winner team=\"TWO\" regular=\"false\" reason=\"Regelverletzung von blues: bad guy\"/>")
            testXStream.toXML(GameResult(definition, scores,
                    WinCondition(Team.TWO, Violation.RULE_VIOLATION(InvalidMoveException(object: IMoveMistake {
                        override val message = "kalkül"
                    }, object: IMove {
                        override fun toString() = "TestZug"
                    }))))) shouldBe
                    gameResultXML.replace("</scores>", "</scores>\n  <winner team=\"TWO\" regular=\"false\" reason=\"Regelverletzung von blues: kalkül bei TestZug\"/>")
        }
    }
})
