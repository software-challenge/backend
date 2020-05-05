package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import sc.framework.plugins.Player

class GameResultTest: StringSpec({
    val definition = ScoreDefinition("winner")
    val scoreRegular = PlayerScore(ScoreCause.REGULAR, "", 1)
    val scores = listOf(scoreRegular, PlayerScore(ScoreCause.LEFT, "Player left", 0))
    val winners = listOf(Player(PlayerColor.BLUE, "bluez"))
    "PlayerScore toString with ScoreDefinition" {
        scoreRegular.toString(definition) shouldContain "winner=1"
        val definition2 = ScoreDefinition("winner", "test")
        shouldThrow<IllegalArgumentException> { scoreRegular.toString(definition2) }
    }
    "GameResult XML" {
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
        }
        
        val gameResultWithWinner = Pair(
                GameResult(definition, scores, winners), """
               <sc.shared.GameResult>
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
                       <reason></reason>
                       <parts>
                         <big-decimal>1</big-decimal>
                       </parts>
                     </sc.shared.PlayerScore>
                     <sc.shared.PlayerScore>
                       <cause>LEFT</cause>
                       <reason>Player left</reason>
                       <parts>
                         <big-decimal>0</big-decimal>
                       </parts>
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
                       <reason></reason>
                       <parts>
                         <big-decimal>1</big-decimal>
                       </parts>
                     </sc.shared.PlayerScore>
                     <sc.shared.PlayerScore>
                       <cause>LEFT</cause>
                       <reason>Player left</reason>
                       <parts>
                         <big-decimal>0</big-decimal>
                       </parts>
                     </sc.shared.PlayerScore>
                   </a>
                 </scores>
                 <winners class="kotlin.collections.EmptyList"/>
               </sc.shared.GameResult>""".trimIndent()
        )
        val gameResults = listOf(gameResultWithWinner, gameResultWithoutWinner)
        gameResults.forEach {
            val toXML = xstream.toXML(it.first)
            toXML shouldBe it.second
            xstream.fromXML(it.second) shouldBe it.first
            xstream.fromXML(toXML) shouldBe it.first
        }
    }
})
