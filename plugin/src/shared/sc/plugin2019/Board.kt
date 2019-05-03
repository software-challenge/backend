package sc.plugin2019

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.IBoard
import sc.plugin2019.FieldState.OBSTRUCTED
import sc.plugin2019.util.Constants
import sc.shared.PlayerColor
import java.util.*

/** Spielbrett für Piranhas mit [Constants.BOARD_SIZE]² Feldern.  */
@XStreamAlias(value = "board")
class Board : IBoard {

    @XStreamImplicit(itemFieldName = "fields")
    private var fields: Array<Array<Field>>

    constructor() {
        this.fields = randomFields()
    }

    constructor(boardToClone: Board) {
        this.fields = generateFields { x, y -> boardToClone.fields[x][y].clone() }
    }

    public override fun clone(): Board = Board(this)

    override fun equals(other: Any?): Boolean = other is Board && Arrays.equals(other.fields, this.fields)

    private fun generateFields(generator: (Int, Int) -> Field): Array<Array<Field>> {
        return Array(Constants.BOARD_SIZE) { x ->
            Array(Constants.BOARD_SIZE) { y ->
                generator(x, y)
            }
        }
    }

    /** Erstellt eine zufälliges Spielbrett.  */
    private fun randomFields(): Array<Array<Field>> {
        val fields = generateFields { x, y -> Field(x, y) }

        // Place Piranhas
        for(index in 1 until Constants.BOARD_SIZE - 1) {
            fields[0][index].setPiranha(PlayerColor.RED)
            fields[Constants.BOARD_SIZE - 1][index].setPiranha(PlayerColor.RED)
            fields[index][0].setPiranha(PlayerColor.BLUE)
            fields[index][Constants.BOARD_SIZE - 1].setPiranha(PlayerColor.BLUE)
        }

        // Place Obstacles
        // only consider fields in the middle of the board
        var blockableFields: List<Field> = fields.slice(Constants.OBSTACLES_START..Constants.OBSTACLES_END).flatMap { it.slice(Constants.OBSTACLES_START..Constants.OBSTACLES_END) }
        // set fields with randomly selected coordinates to blocked
        // coordinates may not lay on same horizontal, vertical or diagonal lines with other selected coordinates
        for(i in 0 until Constants.NUM_OBSTACLES) {
            val indexOfFieldToBlock = Math.floor(Math.random() * blockableFields.size).toInt()
            val selectedField = blockableFields[indexOfFieldToBlock]
            selectedField.state = OBSTRUCTED
            blockableFields = blockableFields.filter { field ->
                !(field.x == selectedField.x || field.y == selectedField.y ||
                        field.x - field.y == selectedField.x - selectedField.y ||
                        field.x + field.y == selectedField.x + selectedField.y)
            }
        }
        return fields
    }

    override fun toString() =
            "Board " + fields.joinToString(" ", "[", "]") { column -> column.joinToString(", ", prefix = "[", postfix = "]") { it.toString() } }

    @XStreamOmitField
    private val line = "-".repeat(Constants.BOARD_SIZE + 2)
    fun prettyString(): String {
        val map = Array(Constants.BOARD_SIZE) { StringBuilder("|") }
        fields.forEach {
            it.forEachIndexed { index, field ->
                map[index].append(field.state.asLetter())
            }
        }
        return map.joinToString("\n", line + "\n", "\n" + line) { it.append('|').toString() }
    }

    override fun getField(x: Int, y: Int): Field =
            this.fields[x][y]

}

