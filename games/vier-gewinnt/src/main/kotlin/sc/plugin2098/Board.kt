package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import sc.framework.deepCopy
import sc.plugin2026.util.PiranhaConstants
import kotlin.random.Random

/** Spielbrett für Vier Gewinnt mit [PiranhaConstants.BOARD_WIDTH] * [PiranhaConstants.BOARD_HEIGHT] Feldern.  */
@XStreamAlias(value = "board")
class Board(
    @XStreamImplicit(itemFieldName = "row")
    override val gameField: MutableTwoDBoard<FieldState> = randomFields()
): RectangularBoard<FieldState>(), IBoard {
    
    /** Gibt eine kompakte String-Darstellung des Spielfelds mit Koordinaten
     * und einer zweibuchstabigen Darstellung der Feldzustände zurück. */
    override fun toString() =
        "Board " + gameField.withIndex().joinToString(" ", "[", "]") { row ->
            row.value.withIndex().joinToString(", ", prefix = "[", postfix = "]") {
                "(${it.index}, ${row.index}) " + it.value.toString()
            }
        }
    
    @Transient
    private val line = "-".repeat(PiranhaConstants.BOARD_WIDTH * 2 + 2)
    /** Gibt eine visuell formatierte Darstellung des Spielfelds mit ASCII-Rahmen zurück. */
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
    
    /** Erstellt eine tiefe Kopie dieses [Board] durch Klonen der zugrunde liegenden Felder. */
    override fun clone(): Board {
        //println("Cloning with ${gameField::class.java}: $this")
        return Board(gameField.deepCopy())
    }
    
    /** Gibt das [Team] eines Fisches an [pos] zurück,
     * oder `null` falls sich kein Fisch auf dem Feld befindet. */
    fun getTeam(pos: Coordinates): Team? =
        this[pos].team
    
    /** Gibt eine Zuordnung aller von [team] belegten Felder zu deren Fischgrößen zurück. */
    fun fieldsForTeam(team: ITeam): Map<Coordinates, Int> =
        filterValues { field -> field.team == team }
            .mapValues { (_, field) -> field.size }
    
    /** @suppress */
    companion object {
        /** Erstellt ein Spielbrett **/
        fun randomFields(): MutableTwoDBoard<FieldState> {
            val fields = Array(PiranhaConstants.BOARD_HEIGHT) {
                Array(PiranhaConstants.BOARD_WIDTH) { FieldState.EMPTY }
            }
            return fields
        }
    }
}
