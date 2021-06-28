package sc.shared

import io.kotest.core.spec.style.StringSpec
import sc.api.plugins.TestTeam
import sc.helpers.shouldSerializeTo
import sc.protocol.room.WelcomeMessage

class WelcomeMessageTest: StringSpec({
    "XML Serialization" {
        WelcomeMessage(TestTeam.BLUE) shouldSerializeTo """<welcomeMessage color="${TestTeam.BLUE.name}"/>"""
    }
})