package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.TurnProblem
import sc.shared.InvalidMoveException

class TurnTest: FunSpec({
    lateinit var gameState: GameState
    lateinit var shipONE: Ship
    lateinit var shipTWO: Ship
    
    beforeEach {
        shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
        shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
        gameState = GameState(ships = listOf(shipONE, shipTWO))
    }
    
    test("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(Turn::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.actions.*"))
        }
        
        val serialized = xStream.toXML(Turn(CubeDirection.UP_RIGHT))
        
        serialized shouldBe """<turn direction="UP_RIGHT"/>"""
    }
    
    context("Perform turn actions") {
        test("Rotation on SANDBANK should not allowed") {
            shipONE.position = gameState.board
                    .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
            val turn = Turn(CubeDirection.UP_RIGHT)
            shouldThrow<InvalidMoveException> {
                turn.perform(gameState)
            }.mistake shouldBe TurnProblem.ROTATION_ON_SANDBANK_NOT_ALLOWED
        }
        
        test("Not enough COAL for rotation should not allowed") {
            shipONE.coal = 0
            val turn = Turn(CubeDirection.UP_LEFT)
            shouldThrow<InvalidMoveException> {
                turn.perform(gameState)
            }.mistake shouldBe TurnProblem.NOT_ENOUGH_COAL_FOR_ROTATION
        }
        
        test("Rotation on non existing field should not allowed") {
            shipONE.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector * 2
            val turn = Turn(CubeDirection.UP_RIGHT)
            shouldThrow<InvalidMoveException> {
                turn.perform(gameState)
            }.mistake shouldBe TurnProblem.ROTATION_ON_NON_EXISTING_FIELD
        }
        
        test("Successful turn action should update direction") {
            shipONE.coal = 5
            shipONE.freeTurns = 2
            val previousDirection = shipONE.direction
            val turn = Turn(CubeDirection.UP_LEFT)
            turn.perform(gameState)
            shipONE.direction shouldNotBe previousDirection
        }
    }
})