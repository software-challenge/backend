package sc.plugin2021

import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import sc.plugin2021.util.Constants

class BoardTest : StringSpec({
    "Test correct board initialisation" {
        val board = Board()
        
        board shouldNotBeSameInstanceAs Board()
        board shouldBe Board()
    
        for (x in 0 until Constants.BOARD_SIZE) {
            for (y in 0 until Constants.BOARD_SIZE) {
                board[x, y] shouldNotBeSameInstanceAs Field(Coordinates(x, y), FieldContent.EMPTY)
                board[x, y] shouldBe Field(Coordinates(x, y), FieldContent.EMPTY)
            }
        }
        
        board[0, 0] = FieldContent.RED
        board[1, 3] = FieldContent.GREEN
        board[5, 9] = FieldContent.BLUE
        board[8, 6] = FieldContent.YELLOW
        
        board.toString() shouldBe """
            R-------------------
            --------------------
            --------------------
            -G------------------
            --------------------
            --------------------
            --------Y-----------
            --------------------
            --------------------
            -----B--------------
            --------------------
            --------------------
            --------------------
            --------------------
            --------------------
            --------------------
            --------------------
            --------------------
            --------------------
            --------------------
            
        """.trimIndent()
        
    }
})
