package sc.plugin2021

import sc.api.plugins.IBoard
import sc.plugin2021.util.Constants
import sc.plugin2021.Field

class Board(
        private val gameField: Array<Array<Color>> =
                Array(Constants.BOARD_SIZE) { Array(Constants.BOARD_SIZE) { Color.NONE }}
): IBoard {
    
    override fun getField(x: Int, y: Int): Field =
            get(x, y)
    
    operator fun get(x: Int, y: Int) =
            Field(Coordinates(x, y), gameField[y][x])
    operator fun get(position: Coordinates) =
            get(position.x, position.y)
    
    operator fun set(x: Int, y: Int, color: Color) {
        gameField[y][x] = color
    }
    operator fun set(position: Coordinates, color: Color) =
            set(position.x, position.y, color)
    
    fun compare(other: Board): Set<Field> {
        val changedFields = mutableSetOf<Field>()
        for (y in 0 until Constants.BOARD_SIZE) {
            for (x in 0 until Constants.BOARD_SIZE) {
                if (gameField[y][x] != other.gameField[y][x]) {
                    changedFields += Field(Coordinates(x, y), other.gameField[y][x])
                }
            }
        }
        return changedFields
    }
    
    override fun hashCode(): Int =
            gameField.contentDeepHashCode()
    
    override fun equals(other: Any?): Boolean {
        return other is Board &&
                other.gameField.contentDeepEquals(gameField)
    }
    
    override fun toString(): String {
        return gameField.joinToString(separator = "") {
            "${it.joinToString(separator = "") { it.letter.toString() }}\n"
        }
    }
}
