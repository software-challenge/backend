package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.*
import sc.api.plugins.Coordinates
import kotlin.random.Random
import sc.plugin2023.util.PenguinConstants as Constants

/**
 * Klasse welche eine Spielbrett darstellt. Bestehend aus einem
 * zweidimensionalen Array aus Feldern
 *
 * @author soed
 */
@XStreamAlias(value = "board")
class Board(override val gameField: MutableTwoDBoard<Field> = generateFields()): RectangularBoard<Field>(gameField) {
    
    constructor(board: Board): this(board.gameField.deepCopy())
    
    override fun isValid(coordinates: Coordinates) =
            (coordinates.x + coordinates.y) % 2 == 0 &&
            coordinates.x >= 0 &&
            super.isValid(coordinates.copy(coordinates.x / 2))
    
    /** Prüft, ob auf dieser [position] bereits eine Spielfigur ist. */
    fun isOccupied(position: Coordinates): Boolean =
            this[position].isOccupied
    
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
            HexDirection.values().flatMap { direction ->
                (1 until Constants.BOARD_SIZE).map {
                    Move.run(pos, direction.vector * it)
                }.takeWhile { getOrEmpty(it.to).fish > 0 }
            }
    
    /** Returns a list of the non-null filter outputs */
    fun <T> filterFields(filter: (Field, Coordinates) -> T?): Collection<T> =
            gameField.flatMapIndexed { y, row ->
                row.mapIndexedNotNull { x, field ->
                    filter(field, Coordinates(x * 2 + y % 2, y))
                }
            }
    
    fun getPenguins() =
            filterFields { field, coordinates ->
                field.penguin?.let { Pair(coordinates, it) }
            }
    
    fun getOrEmpty(key: Coordinates?) = key?.let { getOrNull(it) } ?: Field()
    
    override val entries: Set<Map.Entry<Coordinates, Field>>
        get() = filterFields { f, coordinates -> Positioned(coordinates, f) }.toSet()
    
    override fun clone(): Board = Board(this)
    
    companion object {
        /** Generiert ein neues Spielfeld mit zufällig auf dem Spielbrett verteilten Fischen. */
        private fun generateFields(seed: Int = Random.nextInt()): MutableTwoDBoard<Field> {
            var remainingFish = Constants.BOARD_SIZE * Constants.BOARD_SIZE
            val random = Random(seed)
            println("Board Seed: $seed")
            var maxholes = 5
            // Pro Hälfte 32 Felder, mind. 27 Schollen
            // Maximal (64-20)/2 = 22 2-Fisch-Schollen,
            // also immer mindestens 5 1-Fisch-Schollen pro Seite
            return Array(Constants.BOARD_SIZE / 2) {
                Array(Constants.BOARD_SIZE) {
                    val rand = random.nextInt(remainingFish)
                    if(rand < maxholes) {
                        maxholes--
                        Field()
                    } else {
                        val fish = (rand - maxholes) / 20 + 1
                        remainingFish -= fish
                        Field(fish)
                    }
                }
            }.let {
                it + it.reversedArray().map { list ->
                    Array(Constants.BOARD_SIZE) { index -> list[Constants.BOARD_SIZE - index - 1].clone() }
                }
            }
        }
        
    }
}
