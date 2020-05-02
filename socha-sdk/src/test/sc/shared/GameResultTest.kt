package sc.shared

import sc.framework.plugins.Player

import com.thoughtworks.xstream.XStream
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class GameResultTest: StringSpec({
    "convert XML" {
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
            classLoader = GameResult::class.java.classLoader
        }
        val definition = ScoreDefinition().apply { add("winner") }
        val scores: List<PlayerScore> = listOf(
                PlayerScore(ScoreCause.REGULAR, "test"),
                PlayerScore(ScoreCause.LEFT, "second test")
        )
        val winners: List<Player>? = listOf(Player(PlayerColor.BLUE, "bluez"))
        
        val gameResultWithWinner = Pair(
                GameResult(definition, scores, winners), """
               <sc.shared.GameResult>
                  <isRegular_-delegate class="kotlin.SynchronizedLazyImpl" resolves-to="kotlin.InitializedLazyImpl">
                    <value class="boolean">false</value>
                  </isRegular_-delegate>
                  <definition>
                    <fragments>
                      <sc.shared.ScoreFragment>
                        <name>winner</name>
                        <aggregation>SUM</aggregation>
                        <relevantForRanking>true</relevantForRanking>
                      </sc.shared.ScoreFragment>
                    </fragments>
                  </definition>
                  <scores class="java.util.Arrays${'$'}ArrayList">
                    <a class="sc.shared.PlayerScore-array">
                      <sc.shared.PlayerScore>
                        <cause>REGULAR</cause>
                        <reason>test</reason>
                        <parts/>
                      </sc.shared.PlayerScore>
                      <sc.shared.PlayerScore>
                        <cause>LEFT</cause>
                        <reason>second test</reason>
                        <parts/>
                      </sc.shared.PlayerScore>
                    </a>
                  </scores>
                  <winners class="singleton-list">
                    <sc.framework.plugins.Player>
                      <listeners/>
                      <isCanTimeout>false</isCanTimeout>
                      <isShouldBePaused>false</isShouldBePaused>
                      <violated>false</violated>
                      <left>false</left>
                      <softTimeout>false</softTimeout>
                      <hardTimeout>false</hardTimeout>
                      <color>BLUE</color>
                      <displayName>bluez</displayName>
                    </sc.framework.plugins.Player>
                  </winners>
                </sc.shared.GameResult>""".trimIndent()
        )
        val gameResultWithoutWinner = Pair(
                GameResult(definition, scores, emptyList()), """
               <sc.shared.GameResult>
                  <isRegular_-delegate class="kotlin.SynchronizedLazyImpl" resolves-to="kotlin.InitializedLazyImpl">
                    <value class="boolean">false</value>
                  </isRegular_-delegate>
                  <definition>
                    <fragments>
                      <sc.shared.ScoreFragment>
                        <name>winner</name>
                        <aggregation>SUM</aggregation>
                        <relevantForRanking>true</relevantForRanking>
                      </sc.shared.ScoreFragment>
                    </fragments>
                  </definition>
                  <scores class="java.util.Arrays${'$'}ArrayList">
                    <a class="sc.shared.PlayerScore-array">
                      <sc.shared.PlayerScore>
                        <cause>REGULAR</cause>
                        <reason>test</reason>
                        <parts/>
                      </sc.shared.PlayerScore>
                      <sc.shared.PlayerScore>
                        <cause>LEFT</cause>
                        <reason>second test</reason>
                        <parts/>
                      </sc.shared.PlayerScore>
                    </a>
                  </scores>
                  <winners class="kotlin.collections.EmptyList"/>
                </sc.shared.GameResult>""".trimIndent()
        )
//        print(xstream.toXML(gameResultWithWinner.first))
//        print(xstream.toXML(gameResultWithoutWinner.first))
        
        val gameResults = listOf(gameResultWithWinner, gameResultWithoutWinner)
        gameResults.forAll {
            xstream.toXML(it.first) shouldBe it.second
            xstream.fromXML(it.second) shouldBe it.first
            xstream.fromXML(xstream.toXML(it.first)) shouldBe it.first
        }
    }
})
