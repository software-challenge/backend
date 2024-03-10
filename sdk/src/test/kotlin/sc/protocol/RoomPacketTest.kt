package sc.protocol

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import sc.api.plugins.Team
import sc.framework.plugins.Player
import sc.helpers.shouldSerializeTo
import sc.protocol.requests.PauseGameRequest
import sc.protocol.room.GamePaused
import sc.protocol.room.MoveRequest
import sc.protocol.room.RoomPacket
import sc.shared.GameResult
import sc.shared.PlayerScore
import sc.shared.ScoreDefinition

class RoomPacketTest: StringSpec({
    MoveRequest::class.java.simpleName {
        RoomPacket("12345", MoveRequest()) shouldSerializeTo """
            <room roomId="12345">
              <data class="moveRequest"/>
            </room>""".trimIndent()
    }
    GameResult::class.java.simpleName {
        RoomPacket("12345", GamePaused(false)) shouldSerializeTo """
            <room roomId="12345">
              <data class="paused" paused="false">
              </data>
            </room>""".trimIndent()
    }
})