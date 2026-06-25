package sc.plugin2024

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.helpers.shouldSerializeTo
import sc.plugin2024.actions.Accelerate
import sc.plugin2024.actions.Advance
import sc.plugin2024.actions.Turn
import sc.plugin2024.mistake.AdvanceProblem
import sc.shared.InvalidMoveException

class AdvanceTest: FunSpec({
    test("serializes nicely") {
        Advance(5) shouldSerializeTo """<advance distance="5"/>"""
    }
    
    context("perform") {
        val gameState = GameState()
        val shipONE = gameState.currentShip
        
        test("advance by 2") {
            shipONE.movement = 2
            
            Advance(2).perform(gameState)
            
            shipONE.position shouldBe CubeCoordinates(1, -1)
            shipONE.direction shouldBe CubeDirection.RIGHT
            shipONE.movement shouldBe 0
        }
        
        context("drift") {
            test("across current") {
                shouldThrow<InvalidMoveException> {
                    gameState.performMoveDirectly(Move(Turn(CubeDirection.DOWN_RIGHT), Advance(1)))
                }.mistake shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
                listOf(1, 2, 3).forAll {
                    val state = gameState.performMove(Move(Accelerate(it), Advance(it)))
                    (state as GameState).otherShip.position shouldBe CubeCoordinates(-1, it - 1)
                }
                shouldThrow<InvalidMoveException> {
                    gameState.performMoveDirectly(Move(Accelerate(4), Turn(CubeDirection.DOWN_RIGHT), Advance(4)))
                }.mistake shouldBe AdvanceProblem.FIELD_IS_BLOCKED
            }
            test("within current") {
                shipONE.position = CubeCoordinates.ORIGIN
                shouldThrow<InvalidMoveException> {
                    gameState.performMoveDirectly(Move(Advance(1)))
                }.mistake shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
                listOf(1, 2).forAll {
                    val state = gameState.performMove(Move(Accelerate(it), Advance(it)))
                    (state as GameState).otherShip.position shouldBe CubeCoordinates(it, 0)
                }
            }
            test("double crossing") {
                shouldThrow<InvalidMoveException> {
                    gameState.performMove(Move(Accelerate(4), Turn(CubeDirection.DOWN_RIGHT), Advance(2), Turn(CubeDirection.UP_RIGHT), Advance(2)))
                }.mistake shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
                gameState.performMoveDirectly(Move(Accelerate(5), Turn(CubeDirection.DOWN_RIGHT), Advance(2), Turn(CubeDirection.UP_RIGHT), Advance(2)))
                shipONE.position shouldBe CubeCoordinates(1, -1)
            }
        }
        
        test("no movement points") {
            Advance(3).perform(gameState) shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
        }
        
        context("invalid distance") {
            shipONE.movement = 8
            listOf(-2, -1, 0, 7).forAll {
                Advance(it).perform(gameState) shouldBe AdvanceProblem.INVALID_DISTANCE
            }
        }
        
        test("field does not exists") {
            shipONE.movement = 2
            shipONE.direction = CubeDirection.LEFT
            Advance(2).perform(gameState) shouldBe AdvanceProblem.FIELD_IS_BLOCKED
        }
        
        test("field is blocked") {
            val blockedFieldCoordinate = gameState.board
                    .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.ISLAND::class).first()
            
            var takenDirection: CubeDirection? = null
            
            shipONE.position = CubeDirection.values().map { it.vector }.firstOrNull { direction ->
                gameState.board[blockedFieldCoordinate + direction] != null
                        .also { takenDirection = CubeDirection.values().firstOrNull { it.vector == direction } }
            }?.let { blockedFieldCoordinate + it } ?: run {
                throw IllegalStateException("No valid direction found.")
            }
            
            shipONE.direction = takenDirection?.opposite()
                                ?: throw IllegalStateException("No valid opposite direction found.")
            
            // TODO this failed once: https://github.com/software-challenge/backend/actions/runs/5837615628/job/15833410929
            Advance(1).perform(gameState) shouldBe AdvanceProblem.FIELD_IS_BLOCKED
        }
        
        test("backwards move is not possible") {
            shipONE.direction = CubeDirection.DOWN_RIGHT
            Advance(-1).perform(gameState) shouldBe AdvanceProblem.INVALID_DISTANCE
        }
        
        xtest("only one move allowed on sandbank") {
            val sandbankCoordinate = gameState.board
                    .findNearestFieldTypes(CubeCoordinates.ORIGIN, Field.SANDBANK::class).first()
            
            shipONE.position = sandbankCoordinate
            
            shipONE.direction = CubeDirection.values().firstOrNull { direction ->
                val dest = gameState.board[sandbankCoordinate + direction.vector]
                dest != null && dest.isEmpty
            } ?: throw IllegalStateException("No valid direction found.")
            
            Advance(2).perform(gameState) shouldBe AdvanceProblem.MOVEMENT_POINTS_MISSING
        }
        
        context("on opponent") {
            shipONE.position = CubeCoordinates(-1, 0)
            shipONE.direction = CubeDirection.DOWN_LEFT
            
            test("insufficient movement") {
                Advance(1).perform(gameState) shouldBe AdvanceProblem.INSUFFICIENT_PUSH
            }
            
            shipONE.movement = 2
            test("allowed") {
                Advance(1).perform(gameState) shouldBe null
            }
            
            test("ship already in target") {
                Advance(2).perform(gameState) shouldBe AdvanceProblem.SHIP_ALREADY_IN_TARGET
            }
        }
    }
})