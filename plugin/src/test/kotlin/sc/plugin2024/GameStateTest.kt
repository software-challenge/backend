package sc.plugin2024

import com.thoughtworks.xstream.XStream
import io.kotest.assertions.withClue
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
    
    test("hashCode changes after move") {
        val code = gameState.hashCode()
        gameState.ships.first().coal--
        gameState.hashCode() shouldNotBe code
    }
    
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
            val pusher = gameState.currentShip
            val pushed = gameState.otherShip
            pushed.position shouldBe CubeCoordinates(-2, 1)
            pusher.position = pushed.position
            gameState.performMoveDirectly(Move(Push(CubeDirection.DOWN_RIGHT)))
            
            pusher.points shouldBe 0
            pusher.position shouldBe CubeCoordinates(-2, 1)
            pushed.position shouldBe CubeCoordinates(-2, 2)
            gameState.calculatePoints(pushed) shouldBe 1
            pushed.points shouldBe 1
        }
    }
    
    test("getPossiblePushs") {
        gameState.getPossiblePushs().shouldBeEmpty()
        gameState.currentShip.position = gameState.otherShip.position
        gameState.getPossiblePushs().shouldNotBeEmpty()
        gameState.currentShip.movement = 0
        gameState.getPossiblePushs().shouldBeEmpty()
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
            val sandState = GameState(Board(
                    listOf(Segment(CubeDirection.RIGHT, CubeCoordinates.ORIGIN, generateSegment(false, arrayOf())),
                            Segment(CubeDirection.RIGHT, CubeCoordinates(4,0), generateSegment(false, arrayOf(Field.SANDBANK))))
            ))
            sandState.currentShip.position =
                    sandState.board.findNearestFieldTypes(sandState.currentShip.position, Field.SANDBANK::class).first()
            sandState.getPossibleAdvances() shouldContainExactly listOf(Advance(1), Advance(-1))
            sandState.getSensibleMoves() shouldContainExactly listOf(Move(Advance(1)), Move(Advance(-1)))
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
        gameState.turn shouldBe 0
        gameState.currentTeam shouldBe Team.ONE
        gameState.currentShip.position shouldBe CubeCoordinates(-1, -1)
        // TODO sometimes returns extra moves in all directions?
        gameState.getSensibleMoves() shouldHaveSize 8
    }
    
    context("game over on") {
        test("immovable") {
            gameState.board.segments.first().segment[1][3] = Field.BLOCKED
            gameState.otherShip.freeTurns = 0
            gameState.otherShip.coal = 0
            gameState.isOver shouldBe false
            gameState.turn++
            gameState.getSensibleMoves().shouldBeEmpty()
            gameState.isOver shouldBe true
        }
        test("round limit") {
            gameState.turn = 59
            gameState.isOver shouldBe false
            gameState.turn = 60
            gameState.isOver shouldBe true
        }
        test("distance and reaching goal field") {
            gameState.currentShip.position =
                    gameState.board.segments.last().tip
            gameState.board[gameState.currentShip.position] shouldBe Field.GOAL
            withClue("segment distance") {
                gameState.isOver shouldBe true
                gameState.turn++
                gameState.isOver shouldBe true
            }
            withClue("Nachzug") {
                gameState.currentShip.position =
                        gameState.board.segments.takeLast(2).first().tip
                gameState.turn shouldBe 1
                gameState.getSensibleMoves().shouldNotBeEmpty()
                gameState.isOver shouldBe false // Nachzug ermöglichen
            }
            withClue("Gerade Zugzahl") {
                gameState.turn++
                gameState.isOver shouldBe false
                gameState.currentShip.passengers = 2
                gameState.isOver shouldBe true
            }
        }
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