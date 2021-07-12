package sc.protocol

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import sc.helpers.shouldSerializeTo
import sc.helpers.testXStream
import sc.protocol.requests.JoinGameRequest
import sc.protocol.requests.PrepareGameRequest
import sc.shared.SlotDescriptor

class GameProtocolTest: WordSpec({
    PrepareGameRequest::class.java.simpleName should {
        "deserialize with umlauts" {
            val request = testXStream.fromXML("""
            <?xml version="1.0" encoding="UTF-8"?>
            <prepare gameType="swc_2018_hase_und_igel">
              <slot displayName="Häschenschule" canTimeout="true" reserved="true"/>
              <slot displayName="Testhase" canTimeout="true" reserved="true"/>
            </prepare>""".trimIndent())
            request.shouldBeInstanceOf<PrepareGameRequest>()
            request.slotDescriptors[0].displayName shouldBe "Häschenschule"
            request.pause shouldBe false
            request.gameType shouldBe "swc_2018_hase_und_igel"
        }
        "serialize" {
            PrepareGameRequest("testgame", SlotDescriptor("p1", reserved = false), SlotDescriptor("p2")) shouldSerializeTo """
                 <prepare gameType="testgame" pause="false">
                   <slot displayName="p1" canTimeout="true" reserved="false"/>
                   <slot displayName="p2" canTimeout="true" reserved="true"/>
                 </prepare>
            """.trimIndent()
        }
    }
    JoinGameRequest::class.java.simpleName should {
        "serialize minimally" {
            JoinGameRequest(null) shouldSerializeTo "<join/>"
        }
        "serialize with gameType" {
            JoinGameRequest("Häschen") shouldSerializeTo "<join gameType=\"Häschen\"/>"
        }
    }
})
