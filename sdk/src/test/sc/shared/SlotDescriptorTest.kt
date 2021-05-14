package sc.shared

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import sc.helpers.shouldSerializeTo

class SlotDescriptorTest : StringSpec({
    "convert XML" {
        forAll(
                row(SlotDescriptor("Display Name"),
                """<slotDescriptor displayName="Display Name" canTimeout="true" reserved="true"/>"""),
                
                row(SlotDescriptor("name", false),
                """<slotDescriptor displayName="name" canTimeout="false" reserved="true"/>"""),
                
                row(SlotDescriptor("another name", true, false),
                """<slotDescriptor displayName="another name" canTimeout="true" reserved="false"/>""")
        )
        { descriptor, xml ->
            descriptor shouldSerializeTo xml
        }
    }
})
