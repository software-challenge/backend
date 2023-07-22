package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.Team

class ShipTest: FunSpec({
    test("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(Ship::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.actions.*"))
        }
        
        val serialized = xStream.toXML(Ship(CubeCoordinates.ORIGIN, Team.ONE))
        
        serialized shouldBe """<ship team="ONE" points="0" direction="RIGHT" speed="1" coal="6" passengers="0">
  <position q="0" r="0" s="0"/>
</ship>"""
    }
})