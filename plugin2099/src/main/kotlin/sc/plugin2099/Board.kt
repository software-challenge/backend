package sc.plugin2099

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.*
import sc.framework.deepCopy
import sc.plugin2099.util.TicTacToeConstants

val line = "-".repeat(TicTacToeConstants.BOARD_LENGTH * 2 + 2)

/** Spielbrett für Piranhas mit [TicTacToeConstants.BOARD_LENGTH]² Feldern.  */
@XStreamAlias(value = "board")
class Board(
    @XStreamImplicit(itemFieldName = "row")
    override val gameField: MutableTwoDBoard<FieldState> = emptyFields()
): RectangularBoard<FieldState>(), IBoard {
    
    override fun toString() =
        "Board " + gameField.withIndex().joinToString(" ", "[", "]") { row ->
            row.value.withIndex().joinToString(", ", prefix = "[", postfix = "]") {
                "(${it.index}, ${row.index}) " + it.value.toString()
            }
        }
    
    fun prettyString(): String {
        val map = StringBuilder(line)
        gameField.forEach { row ->
            map.append("\n|")
            row.forEach { field ->
                map.append(field.asLetters())
            }
        }
        map.append("\n").append(line)
        return map.toString()
    }
    
    override fun clone(): Board {
        //println("Cloning with ${gameField::class.java}: $this")
        return Board(gameField.deepCopy())
    }
    
    fun getTeam(pos: Coordinates): Team? =
        this[pos].team
    
    
    companion object {
        /** Erstellt ein leeres Spielbrett.  */
        fun emptyFields(): MutableTwoDBoard<FieldState> {
            return Array(TicTacToeConstants.BOARD_LENGTH) {
                Array(TicTacToeConstants.BOARD_LENGTH) { FieldState.EMPTY }
            }
        }
    }
}
