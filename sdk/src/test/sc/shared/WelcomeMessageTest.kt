package sc.shared

import io.kotest.core.spec.style.StringSpec
import sc.api.plugins.ITeam
import sc.helpers.shouldSerializeTo

class WelcomeMessageTest: StringSpec({
    "XML Serialization" {
        val team = object: ITeam {
            override val index: Int = 2
            override fun opponent(): ITeam = this
            override fun toString() = "testi"
        }
        WelcomeMessage(team) shouldSerializeTo """<welcomeMessage color="testi"/>"""
    }
})