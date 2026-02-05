package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo

class ShipTest: FunSpec({
    val shipOne = Ship(CubeCoordinates(-1, -1), Team.ONE)
    test("hashcode differs upon change") {
        val shipOneMoved = shipOne.copy(CubeCoordinates.ORIGIN)
        shipOne shouldNotBe shipOneMoved
        shipOne.hashCode() shouldNotBe shipOneMoved.hashCode()
    }
    test("serializes nicely") {
         shipOne shouldSerializeTo """
            <ship team="ONE" direction="RIGHT" speed="1" coal="6" passengers="0" freeTurns="1" points="0" crashed="false">
              <position q="-1" r="-1" s="2"/>
            </ship>"""
    }
})