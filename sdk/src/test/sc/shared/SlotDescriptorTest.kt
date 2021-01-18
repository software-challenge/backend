package sc.shared

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import sc.helpers.shouldSerializeTo

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
            descriptor shouldSerializeTo xml
        }
    }
})
