package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.IBoard
import sc.plugin2021.util.Constants
import sc.plugin2021.xstream.BoardConverter

/**
 * Das Spielfeld.
 * Es besteht aus 20x20 [Field]s, deren [Color] mit jedem getätigten [Move] angepasst wird.
 * Jedes Feld kann ausgelesen werden.
 */
@XStreamAlias(value = "board")
@XStreamConverter(value = BoardConverter::class)
class Board(
        private val gameField: Array<Array<FieldContent>> =
                Array(Constants.BOARD_SIZE) { Array(Constants.BOARD_SIZE) { FieldContent.EMPTY }}
): IBoard {


    /** Prüft, ob alle Felder [FieldContent.EMPTY] sind. */
    fun isEmpty() = gameField.all { it.all { it == FieldContent.EMPTY } }

    /**
     * Gebe das [Field] an den gegebenen [Coordinates] zurück.
     * @see get
     */
    override fun getField(x: Int, y: Int): Field =
            get(x, y)

    /** Gebe das [Field] an den gegebenen [Coordinates] zurück. */
    operator fun get(x: Int, y: Int) =
            Field(Coordinates(x, y), gameField[y][x])
    /** Gebe das [Field] an den gegebenen [Coordinates] zurück. */
    operator fun get(position: Coordinates) =
            get(position.x, position.y)

    /** Ändere die Farbe des [Field]s zum gegebenen [FieldContent]. */
    operator fun set(x: Int, y: Int, content: FieldContent) {
        gameField[y][x] = content
    }
    /** Ändere die Farbe des [Field]s zum gegebenen [FieldContent]. */
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
}

/** Die Ecken des Spielfelds. */
enum class Corner(val position: Coordinates) {
    /** Die linke obere Ecke des Spielfelds und damit der Ursprung des Koordinatensystems. */
    UPPER_LEFT(Coordinates(0, 0)) {
        override fun align(area: Vector): Coordinates = position
    },
    /** Die rechte obere Ecke, [19, 0]. */
    UPPER_RIGHT(Coordinates(Constants.BOARD_SIZE - 1, 0)) {
        override fun align(area: Vector): Coordinates = Coordinates(position.x - area.dx, position.y)
    },
    /** Die rechte untere Ecke, [19, 19]. */
    LOWER_RIGHT(Coordinates(Constants.BOARD_SIZE - 1, Constants.BOARD_SIZE - 1)) {
        override fun align(area: Vector): Coordinates = position - area
    },
    /** Die linke untere Ecke, [0, 19]. */
    LOWER_LEFT(Coordinates(0, Constants.BOARD_SIZE - 1)) {
        override fun align(area: Vector): Coordinates = Coordinates(position.x, position.y - area.dy)
    };
    
    /** Berechne die [Coordinates], die ein [Piece] haben muss, um in der entsprechenden Ecke platziert zu werden. */
    abstract fun align(area: Vector): Coordinates
    
    companion object {
        /** Gib eine Sammlung der vier Ecken als [Set] zurück. */
        fun asSet(): Set<Coordinates> = values().map { it.position }.toSet()
    }
}
