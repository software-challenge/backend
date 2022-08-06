package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.*
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
    
    constructor(board: Board): this(board.gameField.clone())
    
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

    override fun isValid(coordinates: Coordinates) =
            (coordinates.x + coordinates.y) % 2 == 0 &&
                coordinates.x >= 0 &&
                super.isValid(coordinates.copy(coordinates.x / 2))
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    override operator fun get(x: Int, y: Int) =
            super.get(x / 2, y)
    
    /** Ersetzt die Fische des Feldes durch einen Pinguin.
     * @return Anzahl der ersetzten Fische. */
    operator fun set(position: Coordinates, team: Team?): Int {
        if(!isValid(position))
            outOfBounds(position)
        val field = gameField[position.y][position.x / 2]
        gameField[position.y][position.x / 2] = Field(penguin = team)
        return field.fish
    }
    
    fun possibleMovesFrom(pos: Coordinates) =
        Vector.DoubledHex.directions.flatMap { vector ->
            (1 until Constants.BOARD_SIZE).map {
                Move.run(pos, vector * it)
            }.takeWhile { getOrEmpty(it.to).fish > 0 }
        }
    
    /** Returns a list of the non-null filter outputs */
    fun <T> filterFields(filter: (Field, Coordinates) -> T?): Collection<T> =
            gameField.flatMapIndexed { y, row ->
                row.mapIndexedNotNull { x, field ->
                    filter(field, Coordinates.doubledHex(x, y))
                }
            }
    
    fun getPenguins() =
            filterFields { field, coordinates ->
                field.penguin?.let { Pair(coordinates, it) }
            }
    
    fun getOrEmpty(key: Coordinates?) = key?.let { getOrNull(it) } ?: Field()
    
    override val entries: Set<Map.Entry<Coordinates, Field>>
        get() = filterFields { field, coordinates -> FieldPosition(coordinates, field) }.toSet()
    
    override fun clone(): Board = Board(this)
    
    companion object {
        /** Generiert ein neues Spielfeld mit zufällig auf dem Spielbrett verteilten Fischen. */
        private fun generateFields(seed: Int = Random.nextInt()): TwoDBoard<Field> {
            var remainingFish = 100
            val random = Random(seed)
            // TODO val holes =
            return List(Constants.BOARD_SIZE) {
                MutableList(Constants.BOARD_SIZE) {
                    val fish = random.nextInt(remainingFish) / 30 + 1
                    remainingFish -= fish
                    Field(fish)
                }
            }
        }
        
    }
}