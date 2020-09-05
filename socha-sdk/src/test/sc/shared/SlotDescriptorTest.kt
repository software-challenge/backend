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
                SlotDescriptor() to
                """<slotDescriptor displayName="Unknown" canTimeout="true" shouldBePaused="true"/>""",
                
                SlotDescriptor("Display Name") to
                """<slotDescriptor displayName="Display Name" canTimeout="true" shouldBePaused="true"/>""",
                
                SlotDescriptor("name", false) to
                """<slotDescriptor displayName="name" canTimeout="false" shouldBePaused="true"/>""",
                
                SlotDescriptor("another name", true, false) to
                """<slotDescriptor displayName="another name" canTimeout="true" shouldBePaused="false"/>"""
        )
        
        descriptors.forAll {
            xstream.toXML(it.first) shouldBe it.second
            xstream.fromXML(it.second).toString() shouldBe it.first.toString()
        }
    }
})