package sc.plugin2021

import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import sc.plugin2021.util.Configuration
import sc.plugin2021.Board

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import sc.plugin2021.util.Constants

class BoardTest : StringSpec({
    "Test correct board initialisation" {
        val board = Board()
    
        for (x in 0 until Constants.BOARD_SIZE) {
            for (y in 0 until Constants.BOARD_SIZE) {
                board[x, y] shouldNotBeSameInstanceAs Field(Coordinates(x, y), FieldContent.EMPTY)
                board[x, y] shouldBe Field(Coordinates(x, y), FieldContent.EMPTY)
            }
        }
    }
})
