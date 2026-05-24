package sc.plugin2098

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.plugin2098.util.Connect4WinReason
import sc.shared.InvalidMoveException
import sc.shared.WinReasonTie

class GameStateTest : FunSpec({
    
    fun playColumn(state: GameState, x: Int, amount: Int) {
        repeat(amount) { y ->
            state.performMoveDirectly(Move(Coordinates(x, y)))
        }
    }
    
    context("performing moves") {
        test("places red token for team one and yellow token for team two") {
            val state = GameState()
            
            state.currentTeam shouldBe Team.ONE
            state.performMoveDirectly(Move(Coordinates(0, 0)))
            
            state.board[0, 0] shouldBe FieldState.RED
            state.turn shouldBe 1
            state.lastMove shouldBe Move(Coordinates(0, 0))
            
            state.currentTeam shouldBe Team.TWO
            state.performMoveDirectly(Move(Coordinates(1, 0)))
            
            state.board[1, 0] shouldBe FieldState.YELLOW
            state.turn shouldBe 2
            state.lastMove shouldBe Move(Coordinates(1, 0))
            state.currentTeam shouldBe Team.ONE
        }
        
        test("throws InvalidMoveException for an invalid move") {
            val state = GameState()
            
            shouldThrow<InvalidMoveException> {
                state.performMoveDirectly(Move(Coordinates(0, 1)))
            }.mistake shouldBe Connect4MoveMistake.DESTINATION_IN_AIR
        }
        
        test("clone creates an independent board copy") {
            val state = GameState()
            state.performMoveDirectly(Move(Coordinates(0, 0)))
            
            val clone = state.clone()
            clone shouldBe state
            
            clone.performMoveDirectly(Move(Coordinates(1, 0)))
            
            clone.board[1, 0] shouldBe FieldState.YELLOW
            state.board[1, 0] shouldBe FieldState.EMPTY
        }
    }
    
    context("sensible moves") {
        test("returns the lowest free field in every non-full column") {
            val state = GameState()
            
            playColumn(state, x = 0, amount = 2)
            playColumn(state, x = 1, amount = 6)
            
            state.getSensibleMoves().shouldContainExactly(
                Move(Coordinates(0, 2)),
                Move(Coordinates(2, 0)),
                Move(Coordinates(3, 0)),
                Move(Coordinates(4, 0)),
                Move(Coordinates(5, 0)),
                Move(Coordinates(6, 0)),
            )
        }
        
        test("returns no moves when the board is full") {
            val state = GameState()
            
            for (x in 0 until 7) {
                for (y in 0 until 6) {
                    state.board[x, y] = if ((x + y) % 2 == 0) FieldState.RED else FieldState.YELLOW
                }
            }
            
            state.getSensibleMoves() shouldBe emptyList()
        }
    }
    
    context("win condition") {
        test("detects team one win after horizontal four") {
            val state = GameState(
                board = Board().also { board ->
                    board[0, 0] = FieldState.RED
                    board[1, 0] = FieldState.RED
                    board[2, 0] = FieldState.RED
                    board[3, 0] = FieldState.RED
                }
            )
            
            state.isOver shouldBe true
            state.winCondition?.winner shouldBe Team.ONE
            state.winCondition?.reason shouldBe Connect4WinReason.CONNECTED_FOUR
        }
        
        test("detects team two win after vertical four") {
            val state = GameState(
                board = Board().also { board ->
                    board[4, 0] = FieldState.YELLOW
                    board[4, 1] = FieldState.YELLOW
                    board[4, 2] = FieldState.YELLOW
                    board[4, 3] = FieldState.YELLOW
                }
            )
            
            state.isOver shouldBe true
            state.winCondition?.winner shouldBe Team.TWO
            state.winCondition?.reason shouldBe Connect4WinReason.CONNECTED_FOUR
        }
        
        test("returns null while the game is not over") {
            val state = GameState()
            state.performMoveDirectly(Move(Coordinates(0, 0)))
            
            state.isOver shouldBe false
            state.winCondition.shouldBeNull()
        }
        
        test("detects tie when the board is full and no player has connected four") {
            val state = GameState(
                board = Board().also { board ->
                    val pattern = arrayOf(
                        arrayOf(FieldState.RED, FieldState.RED, FieldState.YELLOW, FieldState.YELLOW, FieldState.RED, FieldState.RED, FieldState.YELLOW),
                        arrayOf(FieldState.YELLOW, FieldState.YELLOW, FieldState.RED, FieldState.RED, FieldState.YELLOW, FieldState.YELLOW, FieldState.RED),
                        arrayOf(FieldState.RED, FieldState.RED, FieldState.YELLOW, FieldState.YELLOW, FieldState.RED, FieldState.RED, FieldState.YELLOW),
                        arrayOf(FieldState.YELLOW, FieldState.YELLOW, FieldState.RED, FieldState.RED, FieldState.YELLOW, FieldState.YELLOW, FieldState.RED),
                        arrayOf(FieldState.RED, FieldState.RED, FieldState.YELLOW, FieldState.YELLOW, FieldState.RED, FieldState.RED, FieldState.YELLOW),
                        arrayOf(FieldState.YELLOW, FieldState.YELLOW, FieldState.RED, FieldState.RED, FieldState.YELLOW, FieldState.YELLOW, FieldState.RED),
                    )
                    
                    for (y in 0 until 6) {
                        for (x in 0 until 7) {
                            board[x, y] = pattern[y][x]
                        }
                    }
                }
            )
            
            state.isOver shouldBe true
            state.winCondition?.winner shouldBe null
            state.winCondition?.reason shouldBe WinReasonTie
        }
    }
})