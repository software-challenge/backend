package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.data.forAll

class SlotDescriptorTest : StringSpec({
    "convert XML" {
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
            autodetectAnnotations(true)
        }
        forAll(
                row(SlotDescriptor(),
                """<slotDescriptor displayName="Unknown" canTimeout="true" shouldBePaused="true"/>"""),
                
                row(SlotDescriptor("Display Name"),
                """<slotDescriptor displayName="Display Name" canTimeout="true" shouldBePaused="true"/>"""),
                
                row(SlotDescriptor("name", false),
                """<slotDescriptor displayName="name" canTimeout="false" shouldBePaused="true"/>"""),
                
                row(SlotDescriptor("another name", true, false),
                """<slotDescriptor displayName="another name" canTimeout="true" shouldBePaused="false"/>""")
        )
        { descriptor, xml ->
            xstream.toXML(descriptor) shouldBe xml
            xstream.fromXML(xml).toString() shouldBe descriptor.toString()
        }
    }
})