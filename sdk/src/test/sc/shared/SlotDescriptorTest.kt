package sc.shared

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.data.forAll
import sc.helpers.xStream

class SlotDescriptorTest : StringSpec({
    "convert XML" {
        forAll(
                row(SlotDescriptor(),
                """<slotDescriptor displayName="Unknown" canTimeout="true"/>"""),
                
                row(SlotDescriptor("Display Name"),
                """<slotDescriptor displayName="Display Name" canTimeout="true"/>"""),
                
                row(SlotDescriptor("name", false),
                """<slotDescriptor displayName="name" canTimeout="false"/>"""),
                
                row(SlotDescriptor("another name", true),
                """<slotDescriptor displayName="another name" canTimeout="true"/>""")
        )
        { descriptor, xml ->
            xStream.toXML(descriptor) shouldBe xml
            xStream.fromXML(xml).toString() shouldBe descriptor.toString()
        }
    }
})
