package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.IBoard
import sc.plugin2021.util.Constants
import sc.plugin2021.xstream.BoardConverter

/** Das Spielfeld besteht aus 20x20 Feldern, die jeweils von einer Spielerfarbe belegt sein können. */
@XStreamAlias(value = "board")
@XStreamConverter(value = BoardConverter::class)
class Board(
        private val gameField: Array<Array<FieldContent>> =
                Array(Constants.BOARD_SIZE) { Array(Constants.BOARD_SIZE) { FieldContent.EMPTY }}
): IBoard {

    /** Prüft, ob alle Felder leer sind. */
    fun isEmpty() = gameField.all { it.all { it == FieldContent.EMPTY } }
    
    /** Prüft, ob auf dieser [position] bereits eine Spielerfarbe liegt. */
    fun isObstructed(position: Coordinates): Boolean =
            this[position].content != FieldContent.EMPTY
    
    /**
     * Gibt das Feld an den gegebenen Koordinaten zurück.
     * @see get
     */
    override fun getField(x: Int, y: Int): Field =
            get(x, y)

    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    operator fun get(x: Int, y: Int) =
            Field(Coordinates(x, y), gameField[y][x])
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    operator fun get(position: Coordinates) =
            get(position.x, position.y)

    /** Ändert die Farbe des Feldes. */
    operator fun set(x: Int, y: Int, content: FieldContent) {
        gameField[y][x] = content
    }
    /** Ändert die Farbe des Feldes. */
    operator fun set(position: Coordinates, content: FieldContent) =
            set(position.x, position.y, content)

    /** Vergleicht zwei Spielfelder und gibt eine Liste aller Felder zurück, die sich unterscheiden. */
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

    companion object {
        /** @return ob die gegebene Position innerhalb des Spielfelds liegt. */
        fun contains(position: Coordinates) =
                position.x >= 0 && position.x < Constants.BOARD_SIZE &&
                position.y >= 0 && position.y < Constants.BOARD_SIZE
    }
}

/** Die Ecken des Spielfelds. */
enum class Corner(val position: Coordinates) {
    /** Die linke obere Ecke des Spielfelds und damit der Ursprung des Koordinatensystems. */
    UPPER_LEFT(Coordinates.origin) {
        override fun align(area: Vector): Coordinates = position
    },
    /** Die rechte obere Ecke (19, 0). */
    UPPER_RIGHT(Coordinates(Constants.BOARD_SIZE - 1, 0)) {
        override fun align(area: Vector): Coordinates = Coordinates(position.x - area.dx, position.y)
    },
    /** Die rechte untere Ecke (19, 19). */
    LOWER_RIGHT(Coordinates(Constants.BOARD_SIZE - 1, Constants.BOARD_SIZE - 1)) {
        override fun align(area: Vector): Coordinates = position - area
    },
    /** Die linke untere Ecke (0, 19). */
    LOWER_LEFT(Coordinates(0, Constants.BOARD_SIZE - 1)) {
        override fun align(area: Vector): Coordinates = Coordinates(position.x, position.y - area.dy)
    };
    
    /** Berechne die Koordinaten, die ein Stein haben muss, um in der entsprechenden Ecke platziert zu werden. */
    abstract fun align(area: Vector): Coordinates
}
