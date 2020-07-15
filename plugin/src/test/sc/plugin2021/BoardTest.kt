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
                board[x, y] shouldNotBeSameInstanceAs Field(Coordinates(x, y), Color.NONE)
                board[x, y] shouldBe Field(Coordinates(x, y), Color.NONE)
            }
        }
        
        board[0, 0] = Color.RED
        board[1, 3] = Color.GREEN
        board[5, 9] = Color.BLUE
        board[8, 6] = Color.YELLOW
        
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
    "Check Board comparison" {
        val oldBoard = Board()
        val newBoard = Board()
        
        val changingFields = listOf(
                Field(Coordinates(2, 3), Color.YELLOW),
                Field(Coordinates(19, 2), Color.YELLOW),
                Field(Coordinates(8, 3), Color.YELLOW),
                Field(Coordinates(4, 9), Color.YELLOW)
        )
        changingFields.forEach{
            newBoard[it.coordinates] = it.color
        }
        val changedFields = oldBoard.compare(newBoard)
        
        changedFields shouldBe changingFields
    }
})
