package sc.plugin2024

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import io.kotest.matchers.booleans.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.*
import sc.api.plugins.Coordinates
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.api.plugins.Team
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Push
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.AdvanceProblem
import sc.plugin2024.mistake.MoveMistake
import sc.shared.InvalidMoveException

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
    
    context("getPossibleAdvances") {
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
                    problem shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
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
    
    context("getPossibleActions") {
        test("getPossibleAccelerations") {
            gameState.getPossibleAccelerations(0).size shouldBe 1
            gameState.getPossibleAccelerations(1).size shouldBe 2
        }
        test("getPossibleTurns") {
            gameState.getPossibleTurns(0).shouldHaveSize(2)
            gameState.getPossibleTurns(1).shouldHaveSize(4)
            gameState.getPossibleTurns(2).shouldHaveSize(5)
        }
        test("getPossiblePushs") {
            gameState.getPossiblePushs().shouldBeEmpty()
            gameState.currentShip.position = gameState.otherShip.position
            gameState.getPossiblePushs() shouldHaveSize 4
            gameState.currentShip.movement = 0
            gameState.getPossiblePushs().shouldBeEmpty()
        }
        
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
            ship.freeTurns = 0
            ship.coal = 2
            ship.accelerateBy(3)
            gameState.getSensibleMoves() shouldNotContain Move(Accelerate(-3), Advance(1))
            withClue("falls back to using all coal") {
                val firstSegment = gameState.board.segments.first()
                arrayOf(Coordinates(2, 0), Coordinates(2, 1), Coordinates(0, 2), Coordinates(1, 2)).forEach {
                    firstSegment.fields[it.x][it.y] = Field.ISLAND
                }
                gameState.getSensibleMoves() shouldHaveSingleElement Move(Accelerate(-3), Advance(1))
            }
        }
        test("costly move") {
            ship.accelerateBy(1)
            ship.coal = 0
            ship.freeAcc = 0
            ship.freeTurns = 0
            ship.direction = CubeDirection.DOWN_RIGHT
            gameState.getSensibleMoves() shouldHaveSingleElement Move(Advance(1))
            ship.movement = 3
            gameState.getSensibleMoves() shouldHaveSingleElement Move(Advance(2))
        }
        test("pushing and current") {
            gameState.otherShip.position = CubeCoordinates.ORIGIN + CubeDirection.LEFT.vector
            gameState.getSensibleMoves() shouldContain Move(Accelerate(2), Turn(CubeDirection.DOWN_RIGHT), Advance(1), Push(CubeDirection.DOWN_LEFT))
            
            ship.freeTurns = 0
            ship.direction = CubeDirection.DOWN_RIGHT
            val moves = gameState.getSensibleMoves()
            moves shouldContain Move(Accelerate(2), Advance(1), Push(CubeDirection.DOWN_LEFT))
            moves shouldHaveSize 3
            gameState.iterableMoves().shouldContainAll(moves)
        }
        test("unpushable opponent") {
            val state = GameState(
                    Board(listOf(Segment(CubeDirection.RIGHT, CubeCoordinates.ORIGIN, arrayOf(arrayOf(Field.WATER, Field.WATER, Field.WATER))))),
                    ships = listOf(
                            Ship(CubeCoordinates(-1, -2), Team.ONE),
                            Ship(CubeCoordinates(-1, 0), Team.TWO),
                    )
            )
            state.getSensibleMoves() shouldBe listOf(Move(Turn(CubeDirection.DOWN_RIGHT), Advance(1)))
            state.currentShip.accelerateBy(2)
            state.getSensibleMoves() shouldBe listOf(Move(Accelerate(-2), Turn(CubeDirection.DOWN_RIGHT), Advance(1)))
        }
    }
    
    context("getAllMoves") {
        test("has valid Move") {
            gameState.getAllMoves().hasNext() shouldBe true
            shouldNotThrow<InvalidMoveException> {
                gameState.performMoveDirectly(gameState.getAllMoves().next())
            }
        }
        test("offers turns") {
            gameState.iterableMoves().find { it.actions.first() is Turn }
        }
    }
    
    context("performing move") {
        test("reveals next segment") {
            val straightState = GameState(Board(listOf(Segment.empty(), Segment.empty(CubeCoordinates(4, 0)), Segment.empty(CubeCoordinates(8, 0)))))
            val move = Move(Accelerate(5), Advance(6))
            straightState.iterableMoves() shouldContain move
            
            straightState.performMoveDirectly(move)
            straightState.board.segmentIndex(straightState.otherShip.position) shouldBe 1
            straightState.board.visibleSegments shouldBe 3
        }
        
        test("merges duplicate advances") {
            val straightState = GameState(Board(listOf(Segment.empty(), Segment.empty(CubeCoordinates(4, 0)), Segment.empty(CubeCoordinates(8, 0)))))
            straightState.currentShip.position = CubeCoordinates.ORIGIN
            val actions = arrayOf(Turn(CubeDirection.RIGHT), Turn(CubeDirection.UP_RIGHT), Turn(CubeDirection.RIGHT), Advance(1))
            shouldThrow<InvalidMoveException> { straightState.performMove(Move(*actions)) }.mistake shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
            straightState.performMove(Move(Accelerate(1), *actions))
            shouldThrow<InvalidMoveException> { straightState.performMove(Move(*actions)) }.mistake shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
            straightState.performMove(Move(Accelerate(2), *actions, Advance(1)))
            straightState.performMoveDirectly(Move(Accelerate(2), *actions, Advance(1), Turn(CubeDirection.DOWN_RIGHT)))
            straightState.otherShip.run {
                position shouldBe CubeCoordinates(2, 0)
                coal shouldBe 3
            }
        }
        
        context("current works when board is truncated") {
            val commonBoard = Board(listOf(Segment.empty(),
                    Segment.empty(CubeCoordinates(4, 0)),
                    Segment(CubeDirection.UP_RIGHT, CubeCoordinates(8, -4), generateSegment(true, arrayOf()))))
            val state = GameState(commonBoard)
            val start = CubeCoordinates(1, -1)
            state.currentShip.run {
                position = start
                speed = 2
                movement = 2
            }
            
            val moves = state.getSimpleMoves(1)
            val truncState = state.copy(Board(commonBoard.segments.subList(0, 2), nextDirection = CubeDirection.UP_RIGHT))
            moves shouldContainAll truncState.getSimpleMoves(1)
            listOf("full" to state, "truncated" to truncState).forAll { (name, state) ->
                test(name) {
                    state.checkAdvanceLimit(start, CubeDirection.RIGHT, 5).costUntil(4) shouldBe 5
                    state.clone().checkAdvanceLimit(start, CubeDirection.RIGHT, 5).costUntil(4) shouldBe 5
                    shouldNotThrow<InvalidMoveException> {
                        state.performMove(Move(Accelerate(1), Advance(3)))
                        state.performMove(Move(Accelerate(3), Advance(4)))
                        state.performMove(Move(Accelerate(4), Advance(5)))
                    }
                    shouldThrow<InvalidMoveException> { state.performMove(Move(Accelerate(2), Advance(3))) }.mistake shouldBe MoveMistake.MOVEMENT_POINTS_LEFT
                    shouldThrow<InvalidMoveException> { state.performMove(Move(Accelerate(2), Advance(4))) }.mistake shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
                }
            }
        }
    }
    
    context("game over on") {
        test("both immovable") {
            gameState.board.segments.first().fields[1][3] = Field.ISLAND
            gameState.ships.forEach {
                it.freeTurns = 0
                it.coal = 0
            }
            gameState.performMoveDirectly(Move(Advance(1)))
            gameState.isOver shouldBe false
            gameState.turn shouldBe 2
            gameState.board.segments.first().fields[2][1] = Field.ISLAND
            gameState.ships.forEach {
                it.freeTurns = 0
                it.coal = 0
            }
            gameState.getAllMoves().hasNext() shouldBe false
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
            withClue("Nachzug ermöglichen") {
                // Distanz eliminieren
                gameState.currentShip.position =
                        gameState.board.segments.takeLast(2).first().tip
                
                gameState.turn shouldBe 1
                gameState.getSensibleMoves().shouldNotBeEmpty()
                gameState.getAllMoves().hasNext().shouldBeTrue()
                ship.passengers = 2
                gameState.isOver shouldBe false
            }
            withClue("bei gerader Zugzahl") {
                gameState.advanceTurn()
                gameState.isOver shouldBe true
            }
            withClue("fehlende Passagiere") {
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