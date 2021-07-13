package sc.shared

import io.kotest.core.spec.style.StringSpec
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.protocol.room.WelcomeMessage

class WelcomeMessageTest: StringSpec({
    "XML Serialization" {
        WelcomeMessage(Team.ONE) shouldSerializeTo """<welcomeMessage color="${Team.ONE.name}"/>"""
    }
})