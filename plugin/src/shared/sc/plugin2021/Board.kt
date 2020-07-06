package sc.plugin2021

import sc.api.plugins.IBoard
import sc.api.plugins.IField
import sc.plugin2021.util.Constants
import sc.plugin2021.Field

class Board(
        private val gameField: Array<Array<FieldContent>> =
                Array(Constants.BOARD_SIZE) { Array(Constants.BOARD_SIZE) { FieldContent.EMPTY }}
): IBoard {
    
    override fun getField(x: Int, y: Int) = this[x, y]
    operator fun get(x: Int, y: Int) =
            Field(Coordinates(x, y), gameField[x][y])
    
    fun set(x: Int, y: Int, content: FieldContent) {
        gameField[x][y] = content
    }
    
    override fun hashCode(): Int =
            gameField.contentDeepHashCode()
}
