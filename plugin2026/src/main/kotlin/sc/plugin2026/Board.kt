package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.IBoard
import sc.plugin2026.FieldState.OBSTRUCTED
import sc.plugin2026.util.*
import java.util.*

/** Spielbrett für Piranhas mit [PiranhaConstants.BOARD_LENGTH]² Feldern.  */
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
        return Array(PiranhaConstants.BOARD_LENGTH) { x ->
            Array(PiranhaConstants.BOARD_LENGTH) { y ->
                generator(x, y)
            }
        }
    }
    
    /** Erstellt eine zufälliges Spielbrett.  */
    private fun randomFields(): Array<Array<Field>> {
        val fields = generateFields { x, y -> Field(x, y) }
        
        // Place Piranhas
        for(index in 1 until PiranhaConstants.BOARD_LENGTH - 1) {
            fields[0][index].setPiranha(Team.ONE)
            fields[PiranhaConstants.BOARD_LENGTH - 1][index].setPiranha(Team.ONE)
            fields[index][0].setPiranha(Team.TWO)
            fields[index][PiranhaConstants.BOARD_LENGTH - 1].setPiranha(Team.TWO)
        }
        
        // Place Obstacles
        // only consider fields in the middle of the board
        var blockableFields: List<Field> = fields.slice(PiranhaConstants.OBSTACLES_START..PiranhaConstants.OBSTACLES_END).flatMap { it.slice(PiranhaConstants.OBSTACLES_START..PiranhaConstants.OBSTACLES_END) }
        // set fields with randomly selected coordinates to blocked
        // coordinates may not lay on same horizontal, vertical or diagonal lines with other selected coordinates
        for(i in 0 until PiranhaConstants.NUM_OBSTACLES) {
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
    
    val line = "-".repeat(PiranhaConstants.BOARD_LENGTH + 2)
    fun prettyString(): String {
        val map = Array(PiranhaConstants.BOARD_LENGTH) { StringBuilder("|") }
        fields.forEach {
            it.forEachIndexed { index, field ->
                map[index].append(field.state.asLetter())
            }
        }
        return map.joinToString("\n", line + "\n", "\n" + line) { it.append('|').toString() }
    }
    
    fun getField(x: Int, y: Int): Field =
        this.fields[x][y]
    
}

