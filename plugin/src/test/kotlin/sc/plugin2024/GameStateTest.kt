package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.plugin2024.actions.Advance

class GameStateTest: FunSpec({
    val shipONE = Ship(CubeCoordinates.ORIGIN + CubeDirection.UP_LEFT.vector, Team.ONE)
    val shipTWO = Ship(CubeCoordinates.ORIGIN + CubeDirection.DOWN_LEFT.vector, Team.TWO)
    val gameState = GameState(ships = listOf(shipONE, shipTWO))
    val strippedDownGameState = GameState(ships = listOf(shipONE, shipTWO), board = Board(segments = generateBoard().take(2)))
    
    test("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(GameState::class.java)
            processAnnotations(Segment::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.*"))
        }
        
        val serialized = xStream.toXML(strippedDownGameState)
        
        serialized shouldBe """<state turn="0">
    <board>
    </board>
    <lastMove>
    </lastMove>
    <ships>
        <ship>
        </ship>
        <ship>
        </ship>
    </ships>
</state>"""
    }
    
    test("currentTeam should be determined correctly") {
        strippedDownGameState.startTeam shouldBe Team.ONE
        strippedDownGameState.currentTeam shouldBe strippedDownGameState.startTeam
        strippedDownGameState.determineCurrentTeam() shouldBe strippedDownGameState.currentTeam
        strippedDownGameState.currentShip shouldBe shipONE
        strippedDownGameState.otherShip shouldBe shipTWO
        strippedDownGameState.turn++
        strippedDownGameState.currentTeam shouldBe strippedDownGameState.startTeam.opponent()
        strippedDownGameState.turn++
        strippedDownGameState.currentTeam shouldBe strippedDownGameState.startTeam
    }
    
    test("getPossiblePushs") {
        gameState.getPossiblePushs().shouldBeEmpty()
        gameState.currentShip.position = gameState.otherShip.position
        gameState.getPossiblePushs().shouldNotBeEmpty()
    }
    
    test("getPossibleTurns") {
        gameState.getPossibleTurns(0).shouldHaveSize(2)
        gameState.getPossibleTurns(1).shouldHaveSize(4)
    }
    
    context("getPossibleAdvances") {
        test("from starting position") {
            gameState.getPossibleAdvances().size shouldBe 1
        }
        test("from sandbank")  {
            gameState.currentShip.position =
                    gameState.board.findNearestFieldTypes(gameState.currentShip.position, Field.SANDBANK::class).first()
            gameState.getPossibleAdvances() shouldContainAnyOf listOf(Advance(1), Advance(-1))
        }
    }
    
    test("getPossibleAccelerations") {
        gameState.getPossibleAccelerations(0).size shouldBe 1
        gameState.getPossibleAccelerations(1).size shouldBe 2
    }
    
    context("getPossibleActions") {
        test("from starting position") {
            strippedDownGameState.getPossibleActions(0) shouldHaveSize 12
        }
        test("push") {
            strippedDownGameState.currentShip.position = strippedDownGameState.otherShip.position
            strippedDownGameState.getPossibleActions(1) shouldHaveSize 4
        }
    }
    
    test("getSensibleMoves") {
        val sensibleMoves = strippedDownGameState.getSensibleMoves()
        sensibleMoves shouldHaveSize 7
    }
})