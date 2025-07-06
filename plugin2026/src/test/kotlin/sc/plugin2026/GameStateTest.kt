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

class GameStateTest: FunSpec({
    test("cloning") {
        val state = GameState()
        state.clone() shouldBe state
    }
    context("XML Serialization") {
        test("deserialization") {
            val state = GameState()
            val xml = testXStream.toXML(state)
            xml shouldHaveLineCount 124
            val restate = testXStream.fromXML(xml) as GameState
            restate.startTeam shouldBe Team.ONE
            restate.currentTeam shouldBe Team.ONE
            restate shouldBe state
            restate.board.toString() shouldBe state.board.toString()
            
            val startMove = Move(Coordinates(0, 1), Direction.RIGHT)
            state.performMoveDirectly(startMove)
            restate shouldNotBe  state
            // FIXME restate.board.clone()
            //val clone = restate.clone()
            //clone shouldBe state
            restate.performMoveDirectly(startMove)
            restate shouldBe state
            //clone shouldNotBe state
            restate.performMoveDirectly(Move(Coordinates(1, 0), Direction.RIGHT))
            shouldThrow<InvalidMoveException> {
                restate.performMoveDirectly(startMove)
            }.mistake shouldBe PiranhaMoveMistake.WRONG_START
        }
    }
})