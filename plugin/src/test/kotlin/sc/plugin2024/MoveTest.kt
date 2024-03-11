package sc.plugin2024

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeDirection
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn

class MoveTest: StringSpec({
    "deserializes gracefully" {
        testXStream.fromXML("""
            <move>
              <actions>
                <acceleration acc="1"/>
                <advance distance="2"/>
              </actions>
            </move>""".trimIndent()) shouldBe Move(Accelerate(1), Advance(2))
    }
    "serializes well" {
        Move(Accelerate(2), Advance(2), Turn(CubeDirection.UP_LEFT), Advance(1), Push(CubeDirection.LEFT)) shouldSerializeTo """
            <move>
              <actions>
                <acceleration acc="2"/>
                <advance distance="2"/>
                <turn direction="UP_LEFT"/>
                <advance distance="1"/>
                <push direction="LEFT"/>
              </actions>
            </move>
        """.trimIndent()
    }
})