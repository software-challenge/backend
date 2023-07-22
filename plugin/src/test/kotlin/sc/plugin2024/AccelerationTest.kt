package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.shared.InvalidMoveException

class AccelerationTest: FunSpec({
    
    test("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(Acceleration::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.actions.*"))
        }
        
        val serialized = xStream.toXML(Acceleration(5))
        
        serialized shouldBe """<acceleration acc="5"/>"""
    }
    
    val team: Team = Team.ONE
    
    test("Acceleration should correctly update speed and coal of the ship") {
        val gameState = GameState()
        val ship = Ship(CubeCoordinates.ORIGIN, team)
        val acceleration = Acceleration(3)
        
        ship.coal = 5
        
        acceleration.perform(gameState, ship)
        
        ship.speed shouldBe 3
        ship.coal shouldBe 2
    }
    
    test("Throw InvalidMoveException when accelerating beyond maximum speed") {
        val gameState = GameState()
        val ship = Ship(CubeCoordinates.ORIGIN, team)
        val acceleration = Acceleration(7)
        
        shouldThrow<InvalidMoveException> { acceleration.perform(gameState, ship) }
    }
    
    test("Throw InvalidMoveException when decelerating beyond minimum speed") {
        val gameState = GameState()
        val ship = Ship(CubeCoordinates.ORIGIN, team)
        val deceleration = Acceleration(-2)
        
        shouldThrow<InvalidMoveException> { deceleration.perform(gameState, ship) }
    }
    
    test("Throw InvalidMoveException when accelerating with insufficient coal") {
        val gameState = GameState()
        val ship = Ship(CubeCoordinates.ORIGIN, team)
        val acceleration = Acceleration(3)
        
        ship.coal = 1
        
        shouldThrow<InvalidMoveException> { acceleration.perform(gameState, ship) }
    }
    
    test("Throw InvalidMoveException on a sandbank") {
        val gameState = GameState()
        val sandBankCoordinate: CubeCoordinates = gameState.board
                .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
        val ship = Ship(sandBankCoordinate, team)
        val acceleration = Acceleration(1)
        
        shouldThrow<InvalidMoveException> { acceleration.perform(gameState, ship) }
    }
})