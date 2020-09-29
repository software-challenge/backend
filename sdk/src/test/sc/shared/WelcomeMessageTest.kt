package sc.shared

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import sc.api.plugins.ITeam
import sc.helpers.xStream

class WelcomeMessageTest: StringSpec({
    "XML Serialization" {
        val team = object : ITeam {
            override val index: Int = 2
            override fun opponent(): ITeam = this
            override fun toString() = "testi"
        }
        val message = WelcomeMessage(team)
        val serialized = xStream.toXML(message)
       serialized shouldBe """<welcomeMessage color="testi"/>"""
        val deserialized: WelcomeMessage = xStream.fromXML(serialized) as WelcomeMessage
        deserialized.color shouldBe team.toString()
        deserialized shouldBe message
    }
})