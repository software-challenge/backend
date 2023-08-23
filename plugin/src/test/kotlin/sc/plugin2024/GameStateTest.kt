package sc.plugin2024

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import sc.api.plugins.Coordinates
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.AdvanceProblem

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
        gameState.advanceTurn()
        gameState.currentTeam shouldBe gameState.startTeam.opponent()
        gameState.advanceTurn()
        gameState.currentTeam shouldBe gameState.startTeam
        gameState.advanceTurn()
        gameState.currentTeam shouldBe gameState.startTeam.opponent()
        gameState.performMoveDirectly(Move(Advance(1)))
        gameState.turn shouldBe 4
        gameState.currentTeam shouldBe gameState.startTeam.opponent()
    }
    
    test("reveals segment after move") {
        val state = GameState(Board(listOf(Segment.empty(), Segment.empty(CubeCoordinates(4,0)), Segment.empty(CubeCoordinates(8,0)))))
        val move = Move(Acceleration(5), Advance(6))
        var found = false
        state.getAllMoves().forEachRemaining { if(move == it) found = true }
        found shouldBe true
        
        state.performMoveDirectly(move)
        state.board.segmentIndex(state.otherShip.position) shouldBe 1
        state.board.visibleSegments shouldBe 3
    }
    
    context("points calculation") {
        test("at start") {
            gameState.ships.forAll {
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
        gameState.getPossiblePushs() shouldHaveSize 4
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
        xtest("from sandbank") {
            val sandState = GameState(Board(
                    listOf(Segment(CubeDirection.RIGHT, CubeCoordinates.ORIGIN, generateSegment(false, arrayOf())),
                            Segment(CubeDirection.RIGHT, CubeCoordinates(4, 0), generateSegment(false, arrayOf(Field.SANDBANK))))
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
    
    context("advanceLimit") {
        val ship = gameState.currentShip
        test("from start") {
            gameState.checkAdvanceLimit(ship.position, CubeDirection.DOWN_RIGHT, 1).distance shouldBe 0
            gameState.checkAdvanceLimit(ship.position, CubeDirection.DOWN_RIGHT, 2).distance shouldBe 1
            val furtherInfo = gameState.checkAdvanceLimit(ship.position, CubeDirection.DOWN_RIGHT, 3)
            furtherInfo.costUntil(1) shouldBe 2
            furtherInfo.distance shouldBe 2
            furtherInfo.costUntil(2) shouldBe 3
        }
        test("considers pushing and current") {
            ship.direction = CubeDirection.DOWN_RIGHT
            gameState.otherShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
            
            gameState.checkAdvanceLimit(ship).run {
                distance shouldBe 0
                problem shouldBe AdvanceProblem.NO_MOVEMENT_POINTS
            }
            
            ship.speed = 3
            ship.movement = 3
            gameState.checkAdvanceLimit(ship).run {
                distance shouldBe 1
                costUntil(1) shouldBe 2
                problem shouldBe AdvanceProblem.SHIP_ALREADY_IN_TARGET
            }
        }
    }
    context("getPossibleActions") {
        test("from starting position") {
            gameState.getPossibleActions(0) shouldHaveSize 11
        }
        test("push") {
            gameState.currentShip.position = gameState.otherShip.position
            gameState.board.getFieldInDirection(CubeDirection.UP_LEFT, gameState.currentShip.position).shouldBeNull()
            gameState.getPossibleActions(1) shouldHaveSize 4
        }
    }
    
    context("getSensibleMoves") {
        test("from starting position") {
            gameState.turn shouldBe 0
            gameState.currentTeam shouldBe Team.ONE
            gameState.currentShip.position shouldBe CubeCoordinates(-1, -1)
            gameState.getSensibleMoves() shouldHaveSize 7
        }
        val ship = gameState.currentShip
        test("respects coal") {
            ship.coal = 2
            ship.speed = 4
            ship.movement = 4
            gameState.getSensibleMoves() shouldNotContain Move(Acceleration(-3), Advance(1))
            
            val firstSegment = gameState.board.segments.first()
            arrayOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 1), Coordinates(0, 2)).forEach {
                firstSegment.fields[it.x][it.y] = Field.BLOCKED
            }
            withClue("fall back to using all coal") {
                gameState.getSensibleMoves() shouldHaveSingleElement Move(Acceleration(-3), Advance(1))
            }
        }
        test("pushing and current") {
            gameState.otherShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
            gameState.getSensibleMoves() shouldContain Move(Acceleration(2), Turn(CubeDirection.DOWN_RIGHT), Advance(1), Push(CubeDirection.DOWN_LEFT))
            
            ship.freeTurns = 0
            ship.direction = CubeDirection.DOWN_RIGHT
            val moves = gameState.getSensibleMoves()
            moves shouldContain Move(Acceleration(2), Advance(1), Push(CubeDirection.DOWN_LEFT))
            moves shouldHaveSize 3
        }
        test("costly move") {
            ship.coal = 0
            ship.speed = 2
            ship.movement = 2
            ship.freeAcc = 0
            ship.freeTurns = 0
            ship.direction = CubeDirection.DOWN_RIGHT
            gameState.getSensibleMoves() shouldHaveSingleElement Move(Advance(1))
            ship.movement = 3
            gameState.getSensibleMoves() shouldHaveSingleElement Move(Advance(2))
        }
    }
    
    context("game over on") {
        xtest("immovable") {
            gameState.board.segments.first().fields[1][3] = Field.BLOCKED
            gameState.otherShip.freeTurns = 0
            gameState.otherShip.coal = 0
            gameState.isOver shouldBe false
            gameState.advanceTurn()
            gameState.getSensibleMoves().shouldBeEmpty()
            gameState.isOver shouldBe true
        }
        test("round limit") {
            val endState = GameState(turn = 59)
            endState.isOver shouldBe false
            endState.advanceTurn()
            endState.isOver shouldBe true
        }
        test("distance and reaching goal field") {
            val ship = gameState.currentShip
            ship.position =
                    gameState.board.segments.last().tip
            gameState.board[ship.position] shouldBe Field.GOAL
            withClue("segment distance") {
                gameState.isOver shouldBe true
                gameState.advanceTurn()
                gameState.isOver shouldBe true
            }
            withClue("Nachzug") {
                // Distanz eliminieren
                gameState.currentShip.position =
                        gameState.board.segments.takeLast(2).first().tip
                
                gameState.turn shouldBe 1
                gameState.getSensibleMoves().shouldNotBeEmpty()
                ship.passengers = 2
                gameState.isOver shouldBe false // Nachzug ermöglichen
            }
            withClue("Gerade Zugzahl") {
                gameState.advanceTurn()
                gameState.isOver shouldBe true
                ship.passengers = 1
                gameState.isOver shouldBe false
            }
        }
    }
    
    test("serializes nicely") {
        GameState(Board(listOf())) shouldSerializeTo """
            <state startTeam="ONE" turn="0" currentTeam="ONE">
              <board nextDirection="RIGHT"/>
              <ship team="ONE" points="0" direction="RIGHT" speed="1" coal="6" passengers="0" freeTurns="1">
                <position q="-1" r="-1" s="2"/>
              </ship>
              <ship team="TWO" points="0" direction="RIGHT" speed="1" coal="6" passengers="0" freeTurns="1">
                <position q="-2" r="1" s="1"/>
              </ship>
            </state>"""
    }
    
})