package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.*
import sc.shared.InvalidMoveException
import sc.shared.MoveMistake
import java.util.LinkedList
import kotlin.random.Random
import sc.plugin2023.util.PluginConstants as Constants

/**
 * Klasse welche eine Spielbrett darstellt. Bestehend aus einem
 * zweidimensionalen Array aus Feldern
 *
 * @author soed
 */
@XStreamAlias(value = "board")
class Board(fields: TwoDBoard<Field> = generateFields()): RectangularBoard<Field>(fields) {
    
    constructor(board: Board) : this(board.gameField.clone())
    
    /**
     * Setzt einen Pinguin an gewählte Koordinaten. Diese Methode ist nur für
     * den Server relevant, da keine vollständige Überprüfung auf korrekte Züge
     * durchgeführt wird. Um für einen Spieler einen neuen Pinguin zu setzen,
     * die [perform][sc.plugin2015.SetMove.perform]
     * -Methode benutzen
     *
     * @param x
     * X-Koordinate
     * @param y
     * Y-Koordinate
     * @param penguin
     * Pinguin, der gesetzt werden soll.
     */
    //@Throws(IllegalArgumentException::class)
    //fun putPenguin(x: Int, y: Int, penguin: Penguin?) {
    //    require((x < 0 || y < 0 || x >= Constants.BOARD_SIZE || y >= Constants.BOARD_SIZE || fields[x][y].fish !== 1 || fields[x][y].penguin) == null)
    //    fields[x][y].putPenguin(penguin)
    //}
    ///**
    // * nur für den Server relevant
    // */
    //@Throws(IllegalArgumentException::class)
    //private fun putPenguinMove(x: Int, y: Int, penguin: Penguin) {
    //    require((x < 0 || y < 0 || x >= Constants.BOARD_SIZE || y >= Constants.BOARD_SIZE || fields[x][y].fish <= 0 || fields[x][y].penguin) == null)
    //    fields[x][y].putPenguin(penguin)
    //}
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    override operator fun get(x: Int, y: Int) =
            gameField[y][x / 2]
    
    /** Ersetzt die Fische des Feldes durch einen Pinguin.
     * @return Anzahl der ersetzten Fische. */
    operator fun set(position: Coordinates, team: Team?): Int {
        val field = gameField[position.y][position.x / 2]
        gameField[position.y][position.x / 2] = Field(penguin = team)
        return field.fish
    }

    override val entries: Set<Map.Entry<Coordinates, Field>>
        get() = gameField.flatMapIndexed { y, row ->
            row.mapIndexed { x, field ->
                // TODO really? an anonymous object?
                object: Map.Entry<Coordinates, Field> {
                    override val key = Coordinates.doubledHex(x, y)
                    override val value = field
                }
            }
        }.toSet()
    
    override fun clone(): Board = Board(this)
    
    companion object {
        /** Generiert ein neues Spielfeld mit zufällig auf dem Spielbrett verteilten Fischen. */
        private fun generateFields(seed: Int = Random.nextInt()): TwoDBoard<Field> {
            var remainingFish = 100
            val random = Random(seed)
            // TODO val holes =
            return List(Constants.BOARD_SIZE) {
                MutableList(Constants.BOARD_SIZE) {
                    val fish = random.nextInt(remainingFish) / 40
                    remainingFish -= fish
                    Field(fish)
                }
            }
        }
    
    }
}