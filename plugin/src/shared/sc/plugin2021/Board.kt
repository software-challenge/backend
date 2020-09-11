package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IBoard
import sc.plugin2021.util.Constants

@XStreamAlias(value = "board")
class Board(
        private val gameField: Array<Array<FieldContent>> =
                Array(Constants.BOARD_SIZE) { Array(Constants.BOARD_SIZE) { FieldContent.EMPTY }}
): IBoard {
    
    override fun getField(x: Int, y: Int): Field =
            get(x, y)
    
    operator fun get(x: Int, y: Int) =
            Field(Coordinates(x, y), gameField[y][x])
    operator fun get(position: Coordinates) =
            get(position.x, position.y)
    
    operator fun set(x: Int, y: Int, content: FieldContent) {
        gameField[y][x] = content
    }
    operator fun set(position: Coordinates, content: FieldContent) =
            set(position.x, position.y, content)
    
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
            "${it.joinToString(separator = "  ") { it.letter.toString() }}\n"
        }
    }
}

/** The four corners on the Board, used to calculate the position of a piece in a corner. */
enum class Corner(val position: Coordinates) {
    UPPER_LEFT(Coordinates(0, 0)) {
        override fun align(area: Vector): Coordinates = position
    },
    UPPER_RIGHT(Coordinates(Constants.BOARD_SIZE - 1, 0)) {
        override fun align(area: Vector): Coordinates = Coordinates(position.x - area.dx, position.y)
    },
    LOWER_RIGHT(Coordinates(Constants.BOARD_SIZE - 1, Constants.BOARD_SIZE - 1)) {
        override fun align(area: Vector): Coordinates = position - area
    },
    LOWER_LEFT(Coordinates(0, Constants.BOARD_SIZE - 1)) {
        override fun align(area: Vector): Coordinates = Coordinates(position.x, position.y - area.dy)
    };
    
    /** Returns the position a piece of given area has to be placed at to lie in the respective corner. */
    abstract fun align(area: Vector): Coordinates
    
    companion object {
        /** Returns a set of the positions of all corners. */
        fun asSet(): Set<Coordinates> = values().map { it.position }.toSet()
    }
}
