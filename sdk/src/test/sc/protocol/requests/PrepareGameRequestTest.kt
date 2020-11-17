package sc.protocol.requests

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import sc.helpers.shouldSerializeTo
import sc.helpers.xStream
import sc.shared.SlotDescriptor

class PrepareGameRequestTest: WordSpec({
    PrepareGameRequest::class.java.simpleName should {
        "deserialize" {
            val request = xStream.fromXML("""
            <?xml version="1.0" encoding="UTF-8"?>
            <prepare gameType="swc_2018_hase_und_igel">
              <slot displayName="Häschenschule" canTimeout="true"/>
              <slot displayName="Testhase" canTimeout="true"/>
            </prepare>""".trimIndent())
            request should beInstanceOf(PrepareGameRequest::class)
            (request as PrepareGameRequest).slotDescriptors[0].displayName shouldBe "Häschenschule"
            request.pause shouldBe false
            request.gameType shouldBe "swc_2018_hase_und_igel"
        }
        "serialize" {
            PrepareGameRequest("swc_2021_blokus", SlotDescriptor("p1"), SlotDescriptor("p2")) shouldSerializeTo """
                 <prepare gameType="swc_2021_blokus" pause="false">
                   <slot displayName="p1" canTimeout="true"/>
                   <slot displayName="p2" canTimeout="true"/>
                 </prepare>
            """.trimIndent()
        }
    }
})
