package sc.plugin2026

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.string.shouldHaveLineCount
import sc.helpers.testXStream

class BoardTest: FunSpec({
    val board = Board()
    context("generation") {
        test("obstacles position") {
            board.fieldsEmpty() shouldBe false
        }
    }
    context("serialization") {
        test("board") {
            testXStream.toXML(board) shouldHaveLineCount 122
        }
    }
})