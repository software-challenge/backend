package sc.shared

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.api.plugins.ITeam

class WelcomeMessageTest: StringSpec({
    "XML Serialization" {
        val xstream = getXStream()
        val team = object : ITeam {
            override val index: Int = 2
            override fun opponent(): ITeam = this
            override fun toString() = "testi"
        }
        val message = WelcomeMessage(team)
        val serialized = xstream.toXML(message)
       serialized shouldBe """<welcomeMessage color="testi"/>"""
        val deserialized: WelcomeMessage = xstream.fromXML(serialized) as WelcomeMessage
        deserialized.color shouldBe team.toString()
        deserialized shouldBe message
    }
})