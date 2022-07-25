package sc.protocol

import io.kotest.core.spec.style.FunSpec
import sc.api.plugins.Team
import sc.framework.plugins.Player
import sc.helpers.shouldSerializeTo
import sc.protocol.room.MoveRequest
import sc.protocol.room.RoomPacket
import sc.shared.GameResult
import sc.shared.PlayerScore
import sc.shared.ScoreDefinition

class RoomPacketTest: FunSpec({
    test(MoveRequest::class.java.simpleName) {
        RoomPacket("12345", MoveRequest()) shouldSerializeTo """
            <room roomId="12345">
              <data class="moveRequest"/>
            </room>""".trimIndent()
    }
    test(GameResult::class.java.simpleName) {
        RoomPacket("12345", GameResult(ScoreDefinition("winner", "gamescore"), mapOf(Player(Team.ONE, "Best") to PlayerScore(true, "Won")), Team.ONE)) shouldSerializeTo """
            <room roomId="12345">
              <data class="result">
                <definition>
                  <fragment name="winner">
                    <aggregation>SUM</aggregation>
                    <relevantForRanking>true</relevantForRanking>
                  </fragment>
                  <fragment name="gamescore">
                    <aggregation>AVERAGE</aggregation>
                    <relevantForRanking>true</relevantForRanking>
                  </fragment>
                </definition>
                <scores>
                  <entry>
                    <player name="Best" team="ONE"/>
                    <score cause="REGULAR" reason="Won">
                      <part>2</part>
                    </score>
                  </entry>
                </scores>
                <winner team="ONE"/>
              </data>
            </room>""".trimIndent()
    }
})