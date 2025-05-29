package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.MutableTwoDBoard
import sc.api.plugins.RectangularBoard
import sc.api.plugins.Team
import sc.plugin2026.util.*
import kotlin.math.floor

typealias FieldS = Field

/** Spielbrett für Piranhas mit [PiranhaConstants.BOARD_LENGTH]² Feldern.  */
@XStreamAlias(value = "board")
class Board(gameField: MutableTwoDBoard<FieldS> = randomFields()): RectangularBoard<FieldS>(gameField) {
    
    // TODO later
    //override fun toString() =
    //    "Board " + fields.joinToString(" ", "[", "]") { column -> column.joinToString(", ", prefix = "[", postfix = "]") { //it.toString() } }
    //
    //val line = "-".repeat(PiranhaConstants.BOARD_LENGTH + 2)
    //fun prettyString(): String {
    //    val map = Array(PiranhaConstants.BOARD_LENGTH) { StringBuilder("|") }
    //    fields.forEach {
    //        it.forEachIndexed { index, field ->
    //            map[index].append(field.state.asLetter())
    //        }
    //    }
    //    return map.joinToString("\n", line + "\n", "\n" + line) { it.append('|').toString() }
    //}
    
    override fun clone(): Board =
        Board(Array(gameField.size) { column -> this.gameField[column].clone() })
    
    fun getTeam(pos: Coordinates): Team? =
        this[pos].state.team
    
    companion object {
        /** Erstellt eine zufälliges Spielbrett.  */
        private fun randomFields(): MutableTwoDBoard<FieldS> {
            val fields = generateFields { x, y -> FieldS(x, y) }
            
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
                val indexOfFieldToBlock = floor(Math.random() * blockableFields.size).toInt()
                val selectedField = blockableFields[indexOfFieldToBlock]
                selectedField.state = FieldState.OBSTRUCTED
                blockableFields = blockableFields.filter { field ->
                    !(field.x == selectedField.x || field.y == selectedField.y ||
                      field.x - field.y == selectedField.x - selectedField.y ||
                      field.x + field.y == selectedField.x + selectedField.y)
                }
            }
            return fields
        }
        
        private fun generateFields(generator: (Int, Int) -> FieldS): Array<Array<FieldS>> {
            return Array(PiranhaConstants.BOARD_LENGTH) { x ->
                Array(PiranhaConstants.BOARD_LENGTH) { y ->
                    generator(x, y)
                }
            }
        }
    }
}
