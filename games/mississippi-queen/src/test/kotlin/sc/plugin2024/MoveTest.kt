package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeDirection
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.protocol.room.RoomPacket

class MoveTest: FunSpec({
    context("serialization") {
        test("deserializes gracefully") {
            testXStream.fromXML("""
            <move>
              <acceleration acc="1"/>
              <advance distance="2"/>
            </move>""".trimIndent()) shouldBe Move(Accelerate(1), Advance(2))
        }
        test("serializes well") {
            Move(Accelerate(2), Advance(2), Turn(CubeDirection.UP_LEFT), Advance(1), Push(CubeDirection.LEFT)) shouldSerializeTo """
            <move>
              <acceleration acc="2"/>
              <advance distance="2"/>
              <turn direction="UP_LEFT"/>
              <advance distance="1"/>
              <push direction="LEFT"/>
            </move>
        """.trimIndent()
        }
        test("in RoomPacket") {
            RoomPacket("lol", Move(Advance(1))) shouldSerializeTo """
                <room roomId="lol">
                  <data class="move">
                    <advance distance="1"/>
                  </data>
                </room>
            """.trimIndent()
        }
    }
})