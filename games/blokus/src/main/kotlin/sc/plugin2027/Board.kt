package sc.plugin2027

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamConverter
import sc.api.plugins.*
import sc.framework.deepCopy
import sc.plugin2027.util.BoardConverter
import sc.plugin2027.util.Constants
import kotlin.random.Random

/**
 * Spielbrett für Blokus mit [Constants.BOARD_LENGTH]² Feldern.
 */
@XStreamAlias(value = "board")
@XStreamConverter(value = BoardConverter::class)
class Board(
    override val gameField: MutableTwoDBoard<Field> = randomFields()
): RectangularBoard<Field>(), IBoard {
    
    /**
     * Erstellt ein neues Board, das eine Kopie des übergebenen Boards ist.
     */
    constructor(other: Board) : this(Array(other.gameField.size) { other.gameField[it].clone() })
    
    /**
     * Erstellt ein neues Board, das die übergebenen Felder enthält.
     */
    constructor(vararg fields: Field) : this() {
        fields.forEach {
            set(it.coordinates, it)
        }
    }
    
    /**
     * Prüft, ob alle Felder leer ([FieldContent.EMPTY]) sind.
     *
     * @return true, wenn alle Felder leer sind, sonst false
     */
    override fun isEmpty() =
        gameField.all { row ->
            row.all { it.content == FieldContent.EMPTY }
        }
    
    /**
     * Prüft, ob auf dieser [position] bereits eine Spielerfarbe liegt.
     *
     * @param position die zu prüfende Position
     * @return true, wenn das Feld an der angegebenen Position nicht leer ist, sonst false
     */
    fun isObstructed(position: Coordinates): Boolean =
        this[position].content != FieldContent.EMPTY
    
    override fun toString() =
        "Board " + gameField.withIndex().joinToString(" ", "[", "]") { row ->
            row.value.withIndex().joinToString(", ", prefix = "[", postfix = "]") {
                "(${it.index}, ${row.index}) " + it.value.toString()
            }
        }
    
    /**
     * FIXME: This is essentially the same as toString.
     */
    fun prettyString(): String {
        val map = StringBuilder()
        gameField.forEachIndexed { index, row ->
            if (index > 0) map.append("\n")
            map.append(row.joinToString(separator = " ") { it.content.letter.toString() })
        }
        return map.toString()
    }
    
    /**
     * Erstellt eine Kopie dieses Boards, damit Änderungen an der Kopie das Original nicht beeinflussen.
     */
    override fun clone(): Board {
        return deepCopy()
    }
    
    /**
     * Erstellt eine Kopie dieses Boards, damit Änderungen an der Kopie das Original nicht beeinflussen.
     */
    override fun deepCopy(): Board {
        return Board(gameField.deepCopy())
    }
    
    /**
     * Gibt zurück, welches Team auf dem Feld an den angegebenen Koordinaten liegt, oder null, wenn das Feld leer ist.
     */
    fun getTeam(pos: Coordinates): Team? =
        (this[pos].content).toTeamColor()?.team
    
    /**
     * Vergleicht dieses Board mit einem anderen Board und gibt die Felder zurück, die sich unterscheiden.
     *
     * @param other das andere Board, mit dem verglichen werden soll
     * @return eine Menge von Feldern, die sich zwischen diesem Board und dem anderen Board unterscheiden
     */
    fun compare(other: Board): Set<Field> {
        // Iterate over all fields and compare them, return the ones that are different.
        val differentFields = LinkedHashSet<Field>()
        for (y in gameField.indices) {
            for(x in gameField[y].indices) {
                val field = this[x, y]
                val otherField = other[x, y]
                if(field != otherField) {
                    differentFields.add(otherField)
                }
            }
        }
        return differentFields
    }
    
    companion object {
        /**
         * Erstellt ein zufälliges Spielbrett.
         *
         * @param random wird derzeit nicht genutzt.
         * @return ein neues, leeres Board
         */
        fun randomFields(
            @Suppress("UNUSED_PARAMETER") random: Random = Random.Default,
        ): MutableTwoDBoard<Field> {
            // Erstelle ein Array von Feldern, wobei jedes Feld zuerst mit einem leeren Inhalt gefüllt wird.
            val fields = Array(Constants.BOARD_LENGTH) { y ->
                Array(Constants.BOARD_LENGTH) { x ->
                    Field(Coordinates(x, y), FieldContent.EMPTY)
                }
            }
            
            return fields
        }
        
        /**
         * Prüft, ob die angegebenen Koordinaten innerhalb der Grenzen des Spielfelds liegen.
         *
         * @param position die zu prüfende Position
         * @return ob die gegebene Position innerhalb des Spielfelds liegt.
         */
        @JvmStatic
        fun contains(position: Coordinates) =
            position.x >= 0 && position.x < Constants.BOARD_LENGTH &&
                    position.y >= 0 && position.y < Constants.BOARD_LENGTH
    }
}
