package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push

class GameStateTest: FunSpec({
    val gameState = GameState()
    
    test("currentTeam should be determined correctly") {
        gameState.startTeam shouldBe Team.ONE
        gameState.currentTeam shouldBe gameState.startTeam
        gameState.determineAheadTeam() shouldBe gameState.currentTeam
        gameState.currentShip shouldBe gameState.ships.first()
        gameState.otherShip shouldBe gameState.ships.last()
        gameState.turn++
        gameState.currentTeam shouldBe gameState.startTeam.opponent()
        gameState.turn++
        gameState.currentTeam shouldBe gameState.startTeam
        gameState.turn++
        gameState.currentTeam shouldBe gameState.startTeam.opponent()
        gameState.performMoveDirectly(Move(Advance(1)))
        gameState.turn shouldBe 4
        gameState.currentTeam shouldBe gameState.startTeam.opponent()
    }
    
    context("points") {
        test("at start") {
            forAll<Ship>(gameState.ships) {
                it.points shouldBe 0
                gameState.getPointsForTeam(it.team).first() shouldBe 0
            }
        }
        test("after push") {
            gameState.currentShip.position = gameState.otherShip.position
            gameState.performMoveDirectly(Move(Push(CubeDirection.DOWN_RIGHT)))
            gameState.currentShip.points = 0
            gameState.otherShip.points = 1
        }
    }
    
    test("getPossiblePushs") {
        gameState.getPossiblePushs().shouldBeEmpty()
        gameState.currentShip.position = gameState.otherShip.position
        gameState.getPossiblePushs().shouldNotBeEmpty()
    }
    
    test("getPossibleTurns") {
        gameState.getPossibleTurns(0).shouldHaveSize(2)
        gameState.getPossibleTurns(1).shouldHaveSize(4)
        gameState.getPossibleTurns(2).shouldHaveSize(5)
    }
    
    context("getPossibleAdvances") {
        test("from starting position") {
            gameState.getPossibleAdvances() shouldHaveSingleElement Advance(1)
        }
        test("from sandbank") {
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
            gameState.getPossibleActions(0) shouldHaveSize 11
        }
        test("push") {
            gameState.currentShip.position = gameState.otherShip.position
            gameState.getPossibleActions(1) shouldHaveSize 4
        }
    }
    
    test("getSensibleMoves") {
        gameState.currentShip.position shouldBe CubeCoordinates(-1, -1)
        gameState.getSensibleMoves() shouldHaveSize 8
    }
    
    
    xtest("serializes nicely") {
        val xStream = XStream().apply {
            processAnnotations(GameState::class.java)
            processAnnotations(Segment::class.java)
            XStream.setupDefaultSecurity(this)
            allowTypesByWildcard(arrayOf("sc.plugin2024.*"))
        }
        
        val serialized = xStream.toXML(gameState)
        
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
    
})