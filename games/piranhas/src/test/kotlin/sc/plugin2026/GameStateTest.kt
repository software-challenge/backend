package sc.plugin2026

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.string.*
import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.Team
import sc.helpers.testXStream
import sc.shared.InvalidMoveException
import sc.shared.WinReasonTie
import sc.plugin2026.util.PiranhaConstants

class GameStateTest: FunSpec({
    test("cloning") {
        val state = GameState()
        state.clone() shouldBe state
    }
    context("XML Serialization") {
        test("deserialization") {
            val state = GameState(firstUnion = Team.TWO)
            val xml = testXStream.toXML(state)
            xml shouldHaveLineCount 124
            val restate = testXStream.fromXML(xml) as GameState
            restate.startTeam shouldBe Team.ONE
            restate.currentTeam shouldBe Team.ONE
            restate shouldBe state
            restate.board.toString() shouldBe state.board.toString()
            restate.firstUnion shouldBe Team.TWO
            
            val startMove = Move(Coordinates(0, 1), Direction.RIGHT)
            state.performMoveDirectly(startMove)
            restate shouldNotBe state
            val clone = restate.clone()
            clone shouldNotBe state
            restate.performMoveDirectly(startMove)
            restate shouldBe state
            clone shouldNotBe state
            restate.performMoveDirectly(Move(Coordinates(1, 0), Direction.RIGHT))
            shouldThrow<InvalidMoveException> {
                restate.performMoveDirectly(startMove)
            }.mistake shouldBe PiranhaMoveMistake.WRONG_START
        }
    }
    
    context("FIRST_UNION Tie-Breaker") {
        context("manuell") {
            val board = Board.EMPTY
            val state = GameState(board = board)
            // Team ONE: zwei benachbarte 1er-Fische (vollständig verbunden)
            board[Coordinates(1, 1)] = FieldState.from(Team.ONE, 1)
            board[Coordinates(2, 1)] = FieldState.from(Team.ONE, 1)
            // Team TWO: zwei benachbarte 1er-Fische (auch verbunden)
            board[Coordinates(7, 7)] = FieldState.from(Team.TWO, 1)
            board[Coordinates(7, 8)] = FieldState.from(Team.TWO, 1)
            
            test("bei Gleichstand entscheidet FIRST_UNION") {
                state.firstUnion = Team.ONE
                
                val win = state.winCondition
                win?.winner shouldBe Team.ONE
                win?.reason shouldBe sc.plugin2026.util.PiranhasWinReason.FIRST_UNION
            }
            
            test("ohne FIRST_UNION bleibt es Unentschieden") {
                // firstUnion bleibt null
                val win = state.winCondition
                win?.winner shouldBe null
                win?.reason shouldBe WinReasonTie
            }
        }
        
        test("FIRST_UNION wird beim ersten verbundenen Team gesetzt und bleibt auch nach zweiter Verbindung bestehen") {
            val board = Board.EMPTY
            val state = GameState(board = board)
            
            // Team ONE: zwei 1er-Fische, die NICHT verbunden sind, aber durch einen 1er-Schritt verbunden werden können
            board[Coordinates(1, 1)] = FieldState.from(Team.ONE, 1)
            board[Coordinates(3, 2)] = FieldState.from(Team.ONE, 1)
            
            // Team TWO: ebenfalls zwei 1er-Fische, die durch einen 1er-Schritt verbunden werden können
            board[Coordinates(7, 7)] = FieldState.from(Team.TWO, 1)
            board[Coordinates(9, 8)] = FieldState.from(Team.TWO, 1)
            
            // Vor den Zügen ist noch kein Team vollständig verbunden
            state.firstUnion shouldBe null
            
            // Zug 1 (Team.ONE zuerst am Zug): (1,1) -> RIGHT (2,1) verbindet die roten Fische vollständig
            state.performMoveDirectly(Move(Coordinates(1, 1), Direction.RIGHT))
            state.firstUnion shouldBe Team.ONE
            
            // Zug 2 (Team.TWO): (7,7) -> RIGHT (8,7) verbindet die blauen Fische, firstUnion bleibt jedoch Team.ONE
            state.performMoveDirectly(Move(Coordinates(7, 7), Direction.RIGHT))
            state.firstUnion shouldBe Team.ONE
        }
    }
})