package sc.shared

import com.thoughtworks.xstream.XStream
import io.kotlintest.specs.StringSpec
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe

class SlotDescriptorTest : StringSpec({
    "convert XML" {
        val xstream = XStream().apply {
            setMode(XStream.NO_REFERENCES)
            autodetectAnnotations(true)
        }
        val descriptors = listOf(
                SlotDescriptor(),
                SlotDescriptor("Display Name"),
                SlotDescriptor("name", false),
                SlotDescriptor("another name", true, false)
        )
        val XMLs = listOf(
                """<slotDescriptor canTimeout="false" shouldBePaused="false"/>""",
                """<slotDescriptor displayName="Display Name" canTimeout="true" shouldBePaused="true"/>""",
                """<slotDescriptor displayName="name" canTimeout="false" shouldBePaused="true"/>""",
                """<slotDescriptor displayName="another name" canTimeout="true" shouldBePaused="false"/>"""
        )
        
        (descriptors zip XMLs).forAll {
            xstream.toXML(it.first) shouldBe it.second
        }
    }
})