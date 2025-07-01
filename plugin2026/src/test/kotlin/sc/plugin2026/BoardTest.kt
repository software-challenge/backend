package sc.plugin2026

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*

class BoardTest: FunSpec({
    context("generation") {
        test("obstacles position") {
            val board = Board(Board.randomFields())
            board.fieldsEmpty() shouldBe false
        }
    }
})