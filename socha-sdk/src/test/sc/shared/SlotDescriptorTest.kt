package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotlintest.data.forall
import io.kotlintest.specs.StringSpec
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.tables.row

class SlotDescriptorTest : StringSpec({
    "convert XML" {
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
            autodetectAnnotations(true)
        }
        forall(
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