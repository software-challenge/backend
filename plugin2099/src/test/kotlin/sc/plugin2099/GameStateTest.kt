package sc.plugin2099

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLineCount
import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.helpers.testXStream
import sc.shared.InvalidMoveException
import sc.shared.MoveMistake

class GameStateTest: FunSpec({
    test("cloning") {
        val state = GameState()
        state.clone() shouldBe state
    }
    context("XML Serialization") {
        test("deserialization") {
            val state = GameState()
            val xml = testXStream.toXML(state)
            xml shouldHaveLineCount 19
            val restate = testXStream.fromXML(xml) as GameState
            restate.startTeam shouldBe Team.ONE
            restate.currentTeam shouldBe Team.ONE
            restate shouldBe state
            restate.board.toString() shouldBe state.board.toString()

            val startMove = Move(Coordinates(0, 1))
            state.performMoveDirectly(startMove)
            restate shouldNotBe state
            restate.performMoveDirectly(startMove)
            restate shouldBe state
            restate.performMoveDirectly(Move(Coordinates(1, 0)))
            shouldThrow<InvalidMoveException> {
                restate.performMoveDirectly(startMove)
            }.mistake shouldBe MoveMistake.DESTINATION_BLOCKED
        }
    }
})