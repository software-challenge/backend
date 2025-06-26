package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.*
import sc.framework.deepCopy
import sc.plugin2026.util.PiranhaConstants
import kotlin.random.Random

/** Spielbrett für Piranhas mit [PiranhaConstants.BOARD_LENGTH]² Feldern.  */
@XStreamAlias(value = "board")
class Board(gameField: MutableTwoDBoard<FieldState> = randomFields()):
    RectangularBoard<FieldState>(gameField), IBoard {
    
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
        Board(gameField.deepCopy())
    
    fun getTeam(pos: Coordinates): Team? =
        this[pos].team
    
    fun fieldsForTeam(team: ITeam): Map<Coordinates, Int> =
        filterValues { field -> field.team == team }
            .mapValues { (_, field) -> field.size }
    
    companion object {
        /** Erstellt ein zufälliges Spielbrett.  */
        private fun randomFields(random: Random = Random.Default): MutableTwoDBoard<FieldState> {
            val fields = Array(PiranhaConstants.BOARD_LENGTH) {
                Array(PiranhaConstants.BOARD_LENGTH) { FieldState.EMPTY }
            }
            
            fun randomSize(): Int {
                // 4:3:2
                val int = random.nextInt(9)
                return when {
                    int < 4 -> 1
                    int < 7 -> 2
                    else -> 3
                }
            }
            
            // Place Piranhas
            for(index in 1 until PiranhaConstants.BOARD_LENGTH - 1) {
                val size1 = randomSize()
                fields[0][index] = FieldState.from(Team.ONE, size1)
                fields[index][0] = FieldState.from(Team.TWO, size1)
                
                val size2 = randomSize()
                fields[PiranhaConstants.BOARD_LENGTH - 1][index] = FieldState.from(Team.ONE, size2)
                fields[index][PiranhaConstants.BOARD_LENGTH - 1] = FieldState.from(Team.TWO, size2)
            }
            
            // Place Obstacles
            // only consider fields in the middle of the board
            val blockableWidth = PiranhaConstants.OBSTACLES_END - PiranhaConstants.OBSTACLES_START + 1
            val blockableSize = blockableWidth * blockableWidth
            // set fields with randomly selected coordinates to blocked
            val obstacles = ArrayList<Coordinates>(PiranhaConstants.NUM_OBSTACLES)
            while(obstacles.size < PiranhaConstants.NUM_OBSTACLES) {
                val index = random.nextInt(blockableSize + 1)
                val pos = Coordinates(
                    PiranhaConstants.OBSTACLES_START + index.rem(blockableWidth),
                    PiranhaConstants.OBSTACLES_START + index.div(blockableWidth)
                )
                // coordinates may not lay on same horizontal, vertical or diagonal lines with other selected coordinates
                if(obstacles.none {
                        it.x == pos.x || it.y == pos.y ||
                        it.x - it.y == pos.x - pos.y ||
                        it.x + it.y == pos.x + pos.y
                    }) {
                    obstacles.add(pos)
                    fields[pos.x][pos.y] = FieldState.SQUID
                }
            }
            return fields
        }
    }
}
