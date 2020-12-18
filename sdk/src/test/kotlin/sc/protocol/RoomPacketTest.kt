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
        RoomPacket("12345", GameResult(ScoreDefinition("something"), listOf(PlayerScore(true, "Won")), Player(Team.ONE, "Best"))) shouldSerializeTo """
            <room roomId="12345">
              <data class="result">
                <definition>
                  <fragment name="something">
                    <aggregation>SUM</aggregation>
                    <relevantForRanking>true</relevantForRanking>
                  </fragment>
                </definition>
                <score cause="REGULAR" reason="Won">
                  <part>2</part>
                </score>
                <winner displayName="Best">
                  <team class="team">ONE</team>
                </winner>
              </data>
            </room>""".trimIndent()
    }
})