package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.framework.plugins.Player

class GameResultTest: StringSpec({
    "convert XML" {
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
        }
        val definition = ScoreDefinition().apply { add("winner") }
        val scores: List<PlayerScore> = listOf(
                PlayerScore(ScoreCause.REGULAR, "test", 1),
                PlayerScore(ScoreCause.LEFT, "second test", 0)
        )
        val winners: List<Player>? = listOf(Player(PlayerColor.BLUE, "bluez"))
        
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
                       <reason>test</reason>
                       <parts>
                         <big-decimal>1</big-decimal>
                       </parts>
                     </sc.shared.PlayerScore>
                     <sc.shared.PlayerScore>
                       <cause>LEFT</cause>
                       <reason>second test</reason>
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
                       <reason>test</reason>
                       <parts>
                         <big-decimal>1</big-decimal>
                       </parts>
                     </sc.shared.PlayerScore>
                     <sc.shared.PlayerScore>
                       <cause>LEFT</cause>
                       <reason>second test</reason>
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
            xstream.toXML(it.first) shouldBe it.second
            xstream.fromXML(it.second) shouldBe it.first
        }
    }
})
